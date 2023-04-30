package edu.sou.cs452.drop;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

abstract class Stmt {
    interface Visitor<R> {
        R visitOrStmt(Or stmt);

        R visitIterStmt(Iter stmt);

        R visitTranslationStmt(Translation stmt);

        R visitRotationStmt(Rotation stmt);

        R visitInitStmt(Init stmt);
    }

    static class Init extends Stmt {
        public final Vector2 p1;
        public final Vector2 p2;

        Init(Vector2 p1, Vector2 p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInitStmt(this);
        }
    }

    // Nested Stmt classes here...
    static class Or extends Stmt {
        final List<Stmt> left;
        final List<Stmt> right;

        Or(List<Stmt> left, List<Stmt> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitOrStmt(this);
        }
    }

    static class Iter extends Stmt {
        Iter(List<Stmt> body) {
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIterStmt(this);
        }

        final List<Stmt> body;
    }

    static class Translation extends Stmt {
        Translation(float u, float v) {
            this.u = u;
            this.v = v;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTranslationStmt(this);
        }

        final float u;
        final float v;
    }

    // Rotation defined by center (u,v) and angle theta
    static class Rotation extends Stmt {
        final float u;
        final float v;
        final float angle;

        Rotation(float u, float v, float angle) {
            this.u = u;
            this.v = v;
            this.angle = angle;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRotationStmt(this);
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
