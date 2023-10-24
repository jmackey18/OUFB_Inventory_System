import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class InventoryManager {
    private final String filename = "LastSavedInventory.txt";
	private final String filename2 = "CurrentInventory.txt";
    private final String commandSeparator = "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
    private final String startPrompt = "\n1. Manage Inventory\n2. Fulfill an order\n3. Add a donation\n4. Leave terminal\n> > > ";
	private ArrayList<String> productNames;
    private ArrayList<Integer> inventoryNumbers;


    public InventoryManager() {
        productNames = new ArrayList<String>();
        inventoryNumbers = new ArrayList<Integer>();
		makeCurrentInventory(filename, filename2);
    }

    public void run() throws IOException {
		doStartPrompt();

		Scanner scan = new Scanner(System.in);
		int input;
		while((input = scan.nextInt()) != 4){
			switch(input) {
				case 1:
					System.out.println(commandSeparator);
					manageInventoryPrompt();
					doStartPrompt();
					break;
				case 2:
					System.out.println(commandSeparator);
					fufillOrderPrompt();
					doStartPrompt();
					break;
				case 3:
					addDonationPrompt();
					doStartPrompt();
					break;
				default:
					if(input != 4){
						System.out.println(commandSeparator);
						System.out.print("Invalid input; please provide me with a valid input : ");
					}
					break;
			}
		};
		leaveTerminal();
		scan.close();
    }
    
    public void makeCurrentInventory(String filename, String filename2) {
        try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while((line = reader.readLine()) != null){
				productNames.add(line.substring(0, line.indexOf(":")));
				inventoryNumbers.add(Integer.parseInt(line.substring(line.indexOf(":")+2)));
			}
			reader.close();

			BufferedWriter writer = new BufferedWriter(new FileWriter(filename2));
			for(int i = 0; i < inventoryNumbers.size(); i++){
				writer.write(productNames.get(i) + ": " + inventoryNumbers.get(i) + System.lineSeparator());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("File(s) cannot be found within the computer.");
		} catch (IOException e) {
			System.out.println("File(s) cannot be read by the program.");
		}
    }

	public void doStartPrompt(){
        System.out.print(commandSeparator + "\nWhat would you like to do?" + startPrompt);
	}

	public void manageInventoryPrompt() {
		Scanner scan = new Scanner(System.in);
		System.out.print("Please select the desired tool:\n1. Show current inventory\n2. Edit product inventory\n3. Add received products\n4. Show target inventory\n5. Edit target inventory\n6. Save inventory\n> > > ");
		int input;
		boolean check = true;
		do {
			input = scan.nextInt();
			switch(input) {
				case 1:
					System.out.println(commandSeparator + "\nDo something 1");
					check = false;
					break;
				case 2:
					System.out.println(commandSeparator + "\nDo something 2"); 
					check = false;
					break;
				case 3:
					System.out.println(commandSeparator + "\nDo something 3"); 
					check = false;
					break;
				case 4:
					System.out.println(commandSeparator + "\nDo something 4"); 
					check = false;
					break;
				case 5:
					System.out.println(commandSeparator + "\nDo something 5"); 
					check = false;
					break;
				case 6:
					System.out.println(commandSeparator + "\nDo something 6"); 
					check = false;
					break;
				default:
					System.out.print("Invalid input; please provide a valid input : ");	
					break;
			}
		} while(check);
	}

	public void fufillOrderPrompt(){

	}

	public void addDonationPrompt(){

	}

	public void leaveTerminal() throws IOException {
		endTask(inventoryNumbers);
	}

	public void exampleChange(){
		for(int i = 0; i < inventoryNumbers.size(); i++){
			inventoryNumbers.set(i, inventoryNumbers.get(i)+25);
			System.out.println(inventoryNumbers.get(i));
		}
	}

    public void endTask(ArrayList<Integer> inventoryNumbers) throws IOException {
		BufferedWriter saveWriter = new BufferedWriter(new FileWriter(filename));
		for(int i = 0; i < inventoryNumbers.size(); i++){
			saveWriter.write(productNames.get(i) + ": " + inventoryNumbers.get(i) + System.lineSeparator());
		}
		saveWriter.close();
		System.out.println(commandSeparator + "\nInventory has been saved; safe to end terminal.");
	}
}
