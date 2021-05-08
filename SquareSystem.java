import java.util.Scanner;

/**
 * Square System
 *
 * <p>
 * This is a class representing a square system.
 *
 * @author Ellie Moore
 * @version 01.29.2021
 */
public class SquareSystem {

    /**
     * A constant Scanner for console input.
     */
    private static final Scanner STDIN;
    static {
        STDIN = new Scanner(System.in);
    }

    /**
     * The coefficient matrix "a"
     */
    private final double[][] a;

    /**
     * The right-hand side vector "b"
     */
    private final double[] b;

    /**
     * A private constructor for a SquareSystem.
     *
     * @param a the coefficient matrix for the system
     * @param b the right-hand vector for the system
     */
    private SquareSystem(double[][] a, double[] b){
        /*
         * If "a" is not square, stop.
         */
        for(int i = 0; i < a.length; i++)
            if (a[i].length != a.length)
                throw new IllegalArgumentException();
        /*
         * If "a" is NxN, and "b" is not Nx1, stop.
         */
        if(b.length != a.length)
            throw new IllegalArgumentException();
        this.a = a;
        this.b = b;
    }

    /**
     * A method to solve this SquareSystem.
     *
     * <p>
     * This method employs LU factorization, forward elimination,
     * and backwards substitution to solve this square system of
     * equations.
     *
     * <p>
     * Once the algorithm finishes, the solution to this system will
     * be contained in the vector "b."
     *
     * @return the instance.
     */
    protected SquareSystem solve(){
        /*
         * Perform LU factorization.
         * Swap rows if necessary to avoid
         * division by zero.
         */
        final int end = a.length - 1;
        for(int k = 0; k < end; k++) {
            for (int i = k + 1; i <= end; i++) {
                if(a[k][k] == 0){
                    int p = i;
                    while(p < end && a[p][k] == 0) p++;
                    if(a[p][k] == 0)
                        throw new ArithmeticException();
                    for(int d = 0; d <= end; d++) {
                        final double g = a[p][d];
                        a[p][d] = a[k][d];
                        a[k][d] = g;
                    }
                    final double g = b[k];
                    b[k] = b[p]; b[p] = g;
                }
                final double f = a[i][k] /= a[k][k];
                for (int j = k + 1; j <= end; j++)
                    a[i][j] -= f * a[k][j];
            }
        }
        /*
         * Perform forward elimination using L.
         */
        for(int n = 0, i = 1; i <= end; i++, n++) {
            for(int j = 0; j <= n; j++)
                b[i] -= a[i][j] * b[j];
        }
        /*
         * Perform back-substitution using U.
         */
        for(int i = end; i >= 0; i--) { int j = end;
            for(; j > i; j--) b[i] -= a[i][j] * b[j];
            b[i] /= a[i][j];
        }
        /*
         * Return the decorated instance.
         */
        return new SolvedSystem(this);
    }

    /**
     * Solved System
     */
    private static final class SolvedSystem extends SquareSystem {

        private SolvedSystem(final SquareSystem ss){
            super(ss.a, ss.b);
        }

        @Override
        protected SquareSystem solve(){
            return this;
        }

        @Override
        public String toString(){
            final StringBuilder sb = new StringBuilder();
            sb.append("\nLU Factorization =\n");
            for(double[] i: super.a){
                for(int j = 0; j < super.a.length; j++){
                    sb.append(String.format("%.2f", i[j]))
                            .append(" ");
                }
                sb.append("\n");
            }
            sb.append("\n");
            for(int i = 0; i < super.b.length; i++){
                sb.append("x").append(i)
                        .append(" = ")
                        .append(String.format("%f", super.b[i]))
                        .append("\n");
            }
            return sb.toString();
        }

    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append("\nA =\n");
        for(double[] i: a){
            for(int j = 0; j < a.length; j++){
                sb.append(String.format("%.2f", i[j]))
                        .append(" ");
            }
            sb.append("\n");
        }
        sb.append("\n");
        for(int i = 0; i < b.length; i++){
            sb.append("b").append(i)
                    .append(" = ")
                    .append(String.format("%.2f", b[i]))
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Main Method.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args){
        System.out.println(
                "Hello, friend. This is a Square System Solver!\nSSS!\n"
        );
        final int len = getInt(
                "Enter n, the dimension of your square system.",
                1,100
        );
        while(running(len));
        System.out.println("Goodbye, friend");
    }

    /**
     * Static program code that handles user input, creates a SquareSystem,
     * and calls solve() on that system.
     *
     * @return whether or not the program counter should repeat this code
     */
    private static boolean running(final int len){
        final String x = getString(
                "Enter the rows of your matrix as a single row " +
                        "with spaces between each entry."
        );
        double[][] a = new double[len][len];
        for(int i = 0, s = 0; i < len; i++){
            for(int j = 0; j < len; j++, s++){
                final int p = s;
                while(s < x.length() && x.charAt(s) != ' ')
                    s++;
                try {
                    a[i][j] = Double.parseDouble(x.substring(p, s));
                } catch (RuntimeException e){
                    System.out.println("Invalid input. Try again.");
                    return true;
                }
            }
        }
        double[] b = new double[len];
        final String y = getString(
                "Enter your righthand-side vector as a single row " +
                        "with spaces between each entry."
        );
        for(int i = 0, s = 0; i < len; i++, s++){
            final int p = s;
            while(s < y.length() && y.charAt(s) != ' ')
                s++;
            try {
                b[i] = Double.parseDouble(y.substring(p, s));
            } catch (RuntimeException e){
                System.out.println("Invalid input. Try again.");
                return true;
            }
        }
        final SquareSystem ss = new SquareSystem(a, b);
        System.out.println(ss);
        try {
            System.out.println(ss.solve());
        } catch(ArithmeticException e){
            System.out.println(
                    "This system has infinitely many solutions. Try Again."
            );
            return true;
        }
        return false;
    }

    /**
     * A method to get a trimmed String from the user.
     *
     * @param prompt the prompt message
     * @return a user-entered String
     */
    private static String getString(final String prompt){
        System.out.println(prompt);
        System.out.print(">> ");
        return STDIN.nextLine().trim();
    }

    /**
     * A method to get an integer from the user.
     *
     * @param prompt the prompt message
     * @param lowerBound the upper bound
     * @param upperBound the lower bound
     * @return a user-entered Integer
     */
    private static int getInt(final String prompt,
                              final int lowerBound,
                              final int upperBound) {
        System.out.printf(
                prompt + " (%d <= n <= %d)\n",lowerBound, upperBound
        );
        int input;
        do {
            System.out.printf(">> ");
            while (!STDIN.hasNextInt()) {
                System.out.println("Lets try that again...");
                System.out.printf(">> ");
                STDIN.next();
                if (STDIN.hasNextLine()) STDIN.nextLine();
            }
            input = STDIN.nextInt();
            if (STDIN.hasNextLine()) STDIN.nextLine();
            if (input < lowerBound || input > upperBound)
                System.out.println("Lets try that again...");
        } while (input < lowerBound || input > upperBound);
        return input;
    }

}
