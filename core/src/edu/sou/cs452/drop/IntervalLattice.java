package edu.sou.cs452.drop;

public class IntervalLattice {
    public static class Interval {
        public final float lowerBound;
        public final float upperBound;

        public Interval(float lowerBound, float upperBound) {
            if (lowerBound > upperBound) {
                throw new IllegalArgumentException("Lower bound cannot be greater than upper bound.");
            }
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        @Override
        public String toString() {
            return "[" + lowerBound + ", " + upperBound + "]";
        }
    }

    public static Interval translate(Interval start, Interval translation) {
        float newLowerBound = start.lowerBound + translation.lowerBound;
        float newUpperBound = start.upperBound + translation.upperBound;
        return new Interval(newLowerBound, newUpperBound);
    }

    public static Interval join(Interval first, Interval second) {
        float newLowerBound = Math.min(first.lowerBound, second.lowerBound);
        float newUpperBound = Math.max(first.upperBound, second.upperBound);
        return new Interval(newLowerBound, newUpperBound);
    }

    /**
     * Abstracts a float value to an Interval.
     *
     * @param value The float value to be converted.
     * @return The corresponding Interval.
     */
    public static Interval alpha(float value) {
        return new Interval(value, value);
    }
}
