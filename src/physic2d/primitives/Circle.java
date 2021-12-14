package physic2d.primitives;

import org.joml.Vector2f;
import physic2d.rigidbody.Rigidbody2D;

public class Circle {
    private float radius = 1.0f;
    private Rigidbody2D body = null;

    public float getRadius() {
        return this.radius;
    }

    public Vector2f getCenter() {
        return body.getPosition();
    }
}