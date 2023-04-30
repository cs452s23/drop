package edu.sou.cs452.drop;

public class SignLattice {
    public enum SignElement {
        BOTTOM, NEGATIVE, POSITIVE, ZERO, TOP
    }

    public final static SignElement translate(SignElement start, SignElement translation) {
        switch (start) {
            case BOTTOM:
                return translation;
            case ZERO:
                return translation;
            case POSITIVE:
                switch (translation) {
                    case BOTTOM:
                        return SignElement.POSITIVE;
                    case NEGATIVE:
                        return SignElement.TOP;
                    case POSITIVE:
                        return SignElement.POSITIVE;
                    case ZERO:
                        return SignElement.POSITIVE;
                    case TOP:
                        return SignElement.TOP;
                }
            case NEGATIVE:
                switch (translation) {
                    case BOTTOM:
                        return SignElement.NEGATIVE;
                    case NEGATIVE:
                        return SignElement.NEGATIVE;
                    case POSITIVE:
                        return SignElement.TOP;
                    case ZERO:
                        return SignElement.NEGATIVE;
                    case TOP:
                        return SignElement.TOP;
                }
            case TOP:
                return SignElement.TOP;
        }

        throw new IllegalArgumentException("Invalid sign element.");
    }

    public final static SignElement join(SignElement first, SignElement second) {
        switch (first) {
            case BOTTOM:
                return second;
            case ZERO:
                switch (second) {
                    case BOTTOM:
                        return SignElement.ZERO;
                    case NEGATIVE:
                        return SignElement.TOP;
                    case POSITIVE:
                        return SignElement.TOP;
                    case ZERO:
                        return SignElement.ZERO;
                    case TOP:
                        return SignElement.TOP;
                }
            case POSITIVE:
                switch (second) {
                    case BOTTOM:
                        return SignElement.POSITIVE;
                    case NEGATIVE:
                        return SignElement.TOP;
                    case POSITIVE:
                        return SignElement.POSITIVE;
                    case ZERO:
                        return SignElement.TOP;
                    case TOP:
                        return SignElement.TOP;
                }
            case NEGATIVE:
                switch (second) {
                    case BOTTOM:
                        return SignElement.NEGATIVE;
                    case NEGATIVE:
                        return SignElement.NEGATIVE;
                    case POSITIVE:
                        return SignElement.TOP;
                    case ZERO:
                        return SignElement.TOP;
                    case TOP:
                        return SignElement.TOP;
                }
            case TOP:
                return SignElement.TOP;
        }

        throw new IllegalArgumentException("Invalid sign element.");
    }

    /**
     * Abstracts a float value to its corresponding SignElement.
     *
     * @param value The float value to be converted.
     * @return The corresponding SignElement.
     */
    public static SignElement alpha(float value) {
        if (value < 0) {
            return SignElement.NEGATIVE;
        } else if (value > 0) {
            return SignElement.POSITIVE;
        } else {
            return SignElement.ZERO;
        }
    }

}
