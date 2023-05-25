public class CalcObject extends CalcPOA {
    private ORB orb;

    public void setORB(ORB orb) {
        this.orb = orb;
    }

    /**
     * Calculate
     * 
     * @param type the type of the operation, 1 -> +, 2 -> -, 3 -> *, 4 -> /
     * @param a    first number
     * @param b    second number
     * @return calculation result
     */
    @Override
    public int calculate(int type, int a, int b) {
        long result;

        if (type == 1) {
            result = (long) a + b;
        } else if (type == 2) {
            result = (long) a - b;
        } else if (type == 3) {
            result = (long) a * b;
        } else {
            result = (long) a / b;
        }

        if (result >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (result <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else {
            return (int) result;
        }
    }

    @Override
    public void exit() {
        orb.shutdown(false);
    }
}
