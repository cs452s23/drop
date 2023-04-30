package edu.sou.cs452.drop;

import com.badlogic.gdx.math.Rectangle;

public interface AbstractInterpreter {
    /**
     * Returns a rectangle that represents the overapproximation of the behaviors
     * of the given analysis.
     *
     * @return A Rectangle representing the overapproximation of behaviors.
     */
    Rectangle getOverapproximationRectangle();
}
