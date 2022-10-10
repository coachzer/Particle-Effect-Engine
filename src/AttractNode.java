import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class AttractNode extends Sprite {

    public AttractNode(Vector2D location, Vector2D velocity, Vector2D acceleration, double width, double height) {
        super(location, velocity, acceleration, width, height);
    }

    @Override
    public Node createView() {

        Group group = new Group();

        double radius = width / 2;

        Circle circle = new Circle(radius);

        circle.setCenterX(radius);
        circle.setCenterY(radius);

        circle.setStroke(Color.ORANGE);
        circle.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.3));

        group.getChildren().add(circle);

        Text text = new Text("Attractor");
        text.setStroke(Color.WHITESMOKE);
        text.setFill(Color.WHITESMOKE);
        text.setBoundsType(TextBoundsType.LOGICAL);

        text.relocate(radius - text.getLayoutBounds().getWidth() / 2, radius - text.getLayoutBounds().getHeight() / 2);

        group.getChildren().add(text);

        return group;
    }

    public Vector2D attract(Particle particle) {

        // calculate direction of force
        Vector2D dir = Vector2D.subtract(location, particle.location);

        // get distance (constrain distance)
        double distance = dir.magnitude(); // distance between objects
        dir.normalize(); // normalize vector (distance doesn't matter here, we just want this vector for
                         // direction)
        distance = Utility.clamp(distance, 5, 1000); // keep distance within a reasonable range

        // calculate magnitude
        double force = Settings.ATTRACTOR_STRENGTH / (distance * distance); // attracting force is inversely
                                                                            // proportional to distance

        // make a vector out of direction and magnitude
        dir.multiply(force); // get force vector => magnitude * direction

        return dir;

    }

}
