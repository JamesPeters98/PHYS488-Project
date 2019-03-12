import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestScanner {
	
	public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("b0vectors1.csv"));
        scanner.useDelimiter("Next...");
        
        while(scanner.hasNext()){
            System.out.println(scanner.next());
            System.out.println("----------------------");
        }
        scanner.close();
    }

}

