import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 24/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Main {


    public static void main(String[] args){
        BufferedReader buffered = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Glad you could make it, now type either task1, task2 or task3 to view each task.");
        for (;;){

            try{
                String input = buffered.readLine();

                switch (input.toLowerCase()){
                    case "task1":
                        taskOne();
                        break;
                    case "task2":
                        taskTwo();
                        break;
                    case "task3":
                        taskThree();
                        break;
                    default:
                        System.out.println("Invalid task");

                }

            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Error reading from input");
            }

        }
    }

    private static void taskOne() throws Exception {
        System.out.println("Sorting some numbers...");
        Thread.sleep(5000);
        int[] integers = {84, 25, 16, 98, 5, 78, 25, 71, 2, 12, 24, 72};
        for(int i = 0; i <= integers.length -1; i++){
            for(int a = 0; a <= integers.length-2; a++){
                if(integers[a] > integers[a+1]){
                    int last = integers[a];
                    integers[a] = integers[a+1];
                    integers[a + 1] = last;
                }
            }
        }
        for(int i = 0; i <= integers.length - 1; i++){
            System.out.println(integers[i]);
        }
    }

    private static void taskTwo() throws Exception {

        List<Integer> memory = new ArrayList<>();

        BufferedReader buffered = new BufferedReader(new InputStreamReader(System.in));
        for(;;){

            System.out.println("Enter a number (or list - to list, exit - to exit)");
            String input = buffered.readLine();

                if (input != null) {

                    input = input.replace(" ", "");

                    int i;

                    try{
                        i = Integer.parseInt(input);
                    }catch(NumberFormatException e){
                        if(input.equalsIgnoreCase("exit")){
                            System.out.println("Exited.");
                            System.exit(1);
                            break;
                        }

                        if(input.equalsIgnoreCase("list")){
                            System.out.println("Numbers in storage:");
                            memory.forEach(System.out::println);
                            continue;
                        }

                        System.out.println("Invalid number.");
                        continue;
                    }

                    if(i < 0 || i > 100){
                        System.out.println("Number must be in between 0 and 100.");
                        continue;
                    }

                    if(memory.contains(i)){
                        System.out.println("This number is already in storage");
                        continue;
                    }

                    memory.add(i);
                    System.out.println("Number added.");
                }



        }



    }

    private static void taskThree() throws Exception {
        BufferedReader buffered = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nInput an x, y and z value separated by a space, two times");

        Vector vector1 = null;
        Vector vector2 = null;

        for(;;) {

            String input = buffered.readLine();

            if(input != null){

                String[] args = input.split(" ");


                if(vector1 == null || vector2 == null) {

                    if(args.length != 3){
                        System.out.println("Invalid arguments, try again");
                        continue;
                    }

                    for (int i = 0; i < args.length; i++) {
                        int a;

                        try {
                            a = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number at " + args[i]);
                            continue;
                        }


                        if (vector1 == null) {
                            vector1 = new Vector(a);
                            continue;
                        }

                        if (vector1.getY() == -1) {
                            vector1.setY(a);
                            continue;
                        }

                        if (vector1.getZ() == -1) {
                            vector1.setZ(a);
                            System.out.println("Point 1 defined: "+vector1.toString());
                            continue;
                        }

                        if (vector2 == null) {
                            vector2 = new Vector(a);
                            continue;
                        }

                        if (vector2.getY() == -1) {
                            vector2.setY(a);
                            continue;
                        }

                        if (vector2.getZ() == -1) {
                            vector2.setZ(a);
                            System.out.println("Point 2 defined: "+vector2.toString());
                        }
                    }
                    continue;
                }

                System.out.println("Checking to see if they are within a 10 block range of each other..");

                //if(vector1.getX() )





            }


        }



    }

    private static class Vector {

        private int x = -1;
        private int y = -1;
        private int z = -1;

        public Vector(int x){
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }

        public boolean isDefined(){
            return x != -1 && y != -1 && z != -1;
        }

        public String toString(){
            return "x = "+x+", y = "+y+", z = "+z;
        }

    }

}
