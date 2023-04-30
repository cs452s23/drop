package edu.sou.cs452.drop;

import edu.sou.cs452.drop.Stmt.Rotation;
import edu.sou.cs452.drop.Stmt.Init;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import edu.sou.cs452.drop.SignLattice.SignElement;

public class SignInterpreter implements Stmt.Visitor<Void> {
    // The abstract element that represents the result of the analysis.
    private SignElement xSign;
    private SignElement ySign;

    public SignElement getXSign() {
        return xSign;
    }

    public SignElement getYSign() {
        return ySign;
    }

    // === BEGIN: INTERPRETER METHODS ===

    void interpret(List<Stmt> statements) {
        for (Stmt statement : statements) {
            execute(statement);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    // In a static analysis we visit both branches of an OR.
    public Void visitOrStmt(Stmt.Or stmt) {
        for (Stmt s : stmt.left) {
            execute(s);
        }
        for (Stmt s : stmt.right) {
            execute(s);
        }

        return null;
    }

    @Override
    public Void visitIterStmt(Stmt.Iter stmt) {
        // Store the initial signs before entering the loop.
        SignElement initialXSign;
        SignElement initialYSign;

        // Iterate until a fixpoint is reached (xSign and ySign do not change).
        do {
            initialXSign = xSign;
            initialYSign = ySign;

            // Execute the statements in the loop.
            for (Stmt s : stmt.body) {
                execute(s);
            }

            // Check if the signs have changed.
        } while (xSign != initialXSign || ySign != initialYSign);

        return null;
    }

    @Override
    public Void visitTranslationStmt(Stmt.Translation stmt) {
        SignElement uSign = SignLattice.alpha(stmt.u);
        SignElement vSign = SignLattice.alpha(stmt.v);

        xSign = SignLattice.translate(xSign, uSign);
        ySign = SignLattice.translate(ySign, vSign);

        return null;
    }

    @Override
    public Void visitRotationStmt(Rotation stmt) {

        xSign = SignElement.TOP;
        ySign = SignElement.TOP;

        return null;
    }

    @Override
    public Void visitInitStmt(Init stmt) {
        SignElement p1XSign = SignLattice.alpha(stmt.p1.x);
        SignElement p1YSign = SignLattice.alpha(stmt.p1.y);
        SignElement p2XSign = SignLattice.alpha(stmt.p2.x);
        SignElement p2YSign = SignLattice.alpha(stmt.p2.y);

        SignElement xSign = SignLattice.join(p1XSign, p2XSign);
        SignElement ySign = SignLattice.join(p1YSign, p2YSign);

        this.xSign = xSign;
        this.ySign = ySign;

        return null;

    }

    public Rectangle getOverapproximationRectangle() {
        // The overapproximation rectangle is the rectangle that contains all possible
        // points that the shape can be in.
        // For example, if the xSign is [-, +] and the ySign is [-, +], then the
        // overapproximation rectangle is the entire screen.
        // If the xSign is [-, 0] and the ySign is [-, 0], then the overapproximation
        // rectangle is the bottom left quadrant of the screen.

        float xMin = 0;
        float xMax = 0;
        float yMin = 0;
        float yMax = 0;

        switch (xSign) {
            case NEGATIVE:
                xMin = Float.NEGATIVE_INFINITY;
                xMax = 0;
                break;
            case ZERO:
                xMin = 0;
                xMax = 0;
                break;
            case POSITIVE:
                xMin = 0;
                xMax = Float.POSITIVE_INFINITY;
                break;
            case TOP:
                xMin = Float.NEGATIVE_INFINITY;
                xMax = Float.POSITIVE_INFINITY;
                break;
            case BOTTOM:
                assert false;
                break;
        }

        switch (ySign) {
            case NEGATIVE:
                yMin = Float.NEGATIVE_INFINITY;
                yMax = 0;
                break;
            case ZERO:
                yMin = 0;
                yMax = 0;
                break;
            case POSITIVE:
                yMin = 0;
                yMax = Float.POSITIVE_INFINITY;
                break;
            case TOP:
                yMin = Float.NEGATIVE_INFINITY;
                yMax = Float.POSITIVE_INFINITY;
                break;
            case BOTTOM:
                assert false;
                break;
        }

        return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }
}
