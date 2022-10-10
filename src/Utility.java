import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Utility {

    public static double clamp(double value, double min, double max) {

        if (value < min)
            return min;

        return Math.min(value, max);

    }

    public static Image createImage(Node node) {

        WritableImage writableImage;

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        int imageWidth = (int) node.getBoundsInLocal().getWidth();
        int imageHeight = (int) node.getBoundsInLocal().getHeight();

        writableImage = new WritableImage(imageWidth, imageHeight);
        node.snapshot(parameters, writableImage);

        return writableImage;

    }

    public static Image[] preCreateImages() {

        // get number of images
        int count = (int) Settings.PARTICLE_LIFE_SPAN_MAX;

        // create linear gradient lookup image: lifespan 0 -> lifespan max
        Stop[] stops = new Stop[] { new Stop(0, Color.BLACK.deriveColor(1, 1, 1, 0.0)), new Stop(0.3, Color.LIGHTCYAN), new Stop(0.9, Color.BLUEVIOLET), new Stop(1, Color.WHITE)};
        LinearGradient linearGradient = new LinearGradient(0, 0, count, 0, false, CycleMethod.NO_CYCLE, stops);

        Rectangle rectangle = new Rectangle(count,1);
        rectangle.setFill( linearGradient);

        Image lookupImage = createImage(rectangle);

        // pre-create images
        Image[] list = new Image[count];

        double radius = Settings.PARTICLE_WIDTH;

        for (int i = 0; i < count; i++) {

            // get color depending on lifespan
            Color color = lookupImage.getPixelReader().getColor( i, 0);

            // create gradient image with given color
            Circle ball = new Circle(radius);

            RadialGradient gradient1 = new RadialGradient(0, 0, 0, 0, radius, false, CycleMethod.NO_CYCLE, new Stop(0, color.deriveColor(1, 1, 1, 1)), new Stop(1, color.deriveColor(1, 1, 1, 0)));

            ball.setFill(gradient1);

            // create image
            list[i] = Utility.createImage(ball);
        }

        return list;
    }
}
