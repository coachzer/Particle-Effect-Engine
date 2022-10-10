import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    static Random random = new Random();
    static ArrayList<Double> fpsList = new ArrayList<>();

    static long start = System.currentTimeMillis();

    Canvas canvas;
    GraphicsContext graphicsContext;

    Pane layerPane;

    List<AttractNode> allAttractors = new ArrayList<>();
    List<RepelNode> allRepellers = new ArrayList<>();
    List<Particle> allParticles = new ArrayList<>();
    
    AnimationTimer animationTimer;

    Scene scene;

    MouseGestures mouseGestures = new MouseGestures();

    /**
     * Container for pre-created images representing particles
     */
    Image[] images = Utility.preCreateImages();

    public static void main(String[] args) {

        launch(args);

    }

    private static double sumArr(ArrayList<Double> fpsList) {
        double sum = 0;
        for (Double f : fpsList) {
            sum += f;
        }
        return sum;
    }

    public static double averageFps() {
        return sumArr(fpsList) / (fpsList.size() - 1);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();

                canvas = new Canvas(Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
                graphicsContext = canvas.getGraphicsContext2D();

                layerPane = new Pane();
                layerPane.getChildren().addAll(canvas);

                root.setCenter(layerPane);

                scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT, Settings.SCENE_COLOR);

                primaryStage.setTitle("Particle Effect Engine - Sample JavaFX App");
                primaryStage.setScene(scene);
                primaryStage.show();

                Thread taskThread = new Thread(() -> Platform.runLater(() -> {

                    prepareObjects(); // prep objects
                    addListeners(); // prep location listeners
                    startAnimation(); // start the loop

                }));
                taskThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void prepareObjects() {

        // add attractor/s on the screen
        for (int i = 0; i < Settings.ATTRACTOR_COUNT; i++) {
            addAttractors();
        }

        // add repeller/s on the screen
        for (int i = 0; i < Settings.REPELLER_COUNT; i++) {
            addRepellers();
        }

    }

    private void startAnimation() {

        // start animation loop
        animationTimer = new AnimationTimer() {

            double frameCount = 0;
            double prev = 0;

            private void calcFps(long now) {
                if (now - prev > 1_000_000_000) {
                    fpsList.add(frameCount);
                    System.out.println("FPS: " + frameCount);
                    System.out.println("Number of particles: " + allParticles.size());
                    prev = now;
                    frameCount = 0;

                } else {
                    frameCount++;
                }
            }

            @Override
            public void handle(long now) {
                if (Settings.PARALLEL) {
                    // fps counter
                    calcFps(now);
                    multithreaded();
                }

                else {
                    // fps counter
                    calcFps(now);

                    // add new particles
                    for (int i = 0; i < Settings.PARTICLES_PER_ITERATION; i++) {
                        addParticle();
                    }

                    // apply force: gravity
                    allParticles.forEach(sprite -> sprite.applyForce(Settings.FORCE_GRAVITY));

                    // apply attract
                    for (AttractNode attractor : allAttractors) {
                        allParticles.forEach(sprite -> {
                            Vector2D force = attractor.attract(sprite);
                            sprite.applyForce(force);
                        });
                    }

                    // apply repel
                    for (RepelNode repeller : allRepellers) {
                        allParticles.forEach(sprite -> {
                            Vector2D force = repeller.repel(sprite);
                            sprite.applyForce(force);
                        });
                    }

                    // move sprite: apply acceleration, calculate velocity and location
                    allParticles.forEach(Sprite::move);

                    // update in fx scene
                    allAttractors.forEach(Sprite::display);
                    allRepellers.forEach(Sprite::display);

                    // draw all particles on canvas
                    // -----------------------------------------
                    graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    allParticles.forEach(particle -> {
                        Image img = images[particle.getLifeSpan()];
                        graphicsContext.drawImage(img, particle.getLocation().x, particle.getLocation().y);
                    });

                    // life span of particle
                    allParticles.forEach(Sprite::decreaseLifeSpan);

                    // remove all particles that aren't visible anymore
                    removeDeadParticles();

                    // show number of particles
                    graphicsContext.setFill(Color.WHITE);
                    graphicsContext.fillText("Particles: " + allParticles.size(), 1, 10);
                }

            }



        };

        animationTimer.start();
        new TimedExit();
    }

    private void removeDeadParticles() {

        // remove from particle list
        allParticles.removeIf(Sprite::isDead);

    }

    private void addParticle() {

        // random location
        double x = ((Settings.SCENE_WIDTH / 2) + (random.nextDouble() * Settings.EMITTER_WIDTH)) - (Settings.EMITTER_WIDTH >> 1);
        double y = Settings.EMITTER_LOCATION_Y;

        // dimensions
        double width = Settings.PARTICLE_WIDTH;
        double height = Settings.PARTICLE_HEIGHT;

        // create motion data
        Vector2D location = new Vector2D(x, y);

        double vx = random.nextGaussian() * 0.3;
        double vy = random.nextGaussian() * 0.3 - 1.0;
        Vector2D velocity = new Vector2D(vx, vy);

        Vector2D acceleration = new Vector2D(0, 0);

        // create sprite and add to layer
        Particle sprite = new Particle(location, velocity, acceleration, width, height);

        // register sprite
        allParticles.add(sprite);

    }

    // parallel part, javafx handles parallel with the use of parallel stream
    private void multithreaded() {

        Thread thread1 = new Thread(() -> Platform.runLater(() -> {
            // add new particles
            for (int i = 0; i < Settings.PARTICLES_PER_ITERATION; i++) {
                addParticle();
            }
        }));
        thread1.start();
        
        // apply attract
        Thread thread2 = new Thread(() -> Platform.runLater(() -> {
            // apply gravity
            allParticles.stream().parallel().forEach(sprite -> sprite.applyForce(Settings.FORCE_GRAVITY));
            
            for (AttractNode attractor : allAttractors) {
                allParticles.stream().parallel().forEach(sprite -> {
                    Vector2D force = attractor.attract(sprite);
                    sprite.applyForce(force);
                });
            }
        }));
        thread2.start();
        
        // apply repel
        Thread thread3 = new Thread(() -> Platform.runLater(() -> {
            
            for (RepelNode repeller : allRepellers) {
                allParticles.stream().parallel().forEach(sprite -> {
                    Vector2D force = repeller.repel(sprite);
                    sprite.applyForce(force);
                });
            }
        }));
        thread3.start();

        // move sprite: apply acceleration, calculate velocity and location
        allParticles.stream().parallel().forEach(Sprite::move);
        // update in fx scene
        allAttractors.stream().parallel().forEach(Sprite::display);

        allRepellers.stream().parallel().forEach(Sprite::display);
        
        // draw all particles
        Thread thread4 = new Thread(() -> Platform.runLater(() -> {
            
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            allParticles.forEach(particle -> {

                Image img = images[particle.getLifeSpan()];
                graphicsContext.drawImage(img, particle.getLocation().x, particle.getLocation().y);

            });
        }));
        thread4.start();

        // time to live of particle
        allParticles.stream().parallel().forEach(Sprite::decreaseLifeSpan);
        
        // remove all particles that aren't visible anymore
        Thread thread5 = new Thread(() -> 
                Platform.runLater(this::removeDeadParticles));
        thread5.start();

        // show number of particles
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillText("Particles: " + allParticles.size(), 1, 10);

    }

    private void addAttractors() {

        // center attractor
        double x = Settings.SCENE_WIDTH / 2;
        double y = Settings.SCENE_HEIGHT - Settings.SCENE_HEIGHT / 4;

        // dimensions
        double width = 100;
        double height = 100;

        // create motion data
        Vector2D location = new Vector2D(x, y);
        Vector2D velocity = new Vector2D(0, 0);
        Vector2D acceleration = new Vector2D(0, 0);

        // create sprite and add to layer
        AttractNode attractor = new AttractNode(location, velocity, acceleration, width, height);

        // register sprite
        allAttractors.add(attractor);

        layerPane.getChildren().add(attractor);

    }

    private void addRepellers() {

        // center attractor
        double x = Settings.SCENE_WIDTH / 2;
        double y = Settings.SCENE_HEIGHT - Settings.SCENE_HEIGHT / 4 + 110;

        // dimensions
        double width = 100;
        double height = 100;

        // create motion data
        Vector2D location = new Vector2D(x, y);
        Vector2D velocity = new Vector2D(0, 0);
        Vector2D acceleration = new Vector2D(0, 0);

        // create sprite and add to layer
        RepelNode repeller = new RepelNode(location, velocity, acceleration, width, height);

        // register sprite
        allRepellers.add(repeller);

        layerPane.getChildren().add(repeller);
    }

    private void addListeners() {

        // move attractors via mouse
        for (AttractNode attractor : allAttractors) {
            mouseGestures.makeDraggable(attractor);
        }

        // move repeller via mouse
        for (RepelNode sprite : allRepellers) {
            mouseGestures.makeDraggable(sprite);
        }

    }
}