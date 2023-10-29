import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InventoryManager {
	
    private final String lastSavedInventoryFile = "OUFB_Inventory_System/txtfiles/LastSavedInventory.txt";
	private final String currentInventoryFile = "OUFB_Inventory_System/txtfiles/CurrentInventory.txt";
	private final String inventoryTargetFile = "OUFB_Inventory_System/txtfiles/InventoryTarget.txt";
    
	private final String commandSeparator = "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
    private final String startPrompt = "\n1. Manage Inventory\n2. Fulfill an order\n3. Add a donation\n4. Leave terminal\n> > > ";
	
	private ArrayList<String> productNames;
    private ArrayList<Integer> inventoryNumbers;
	private Scanner scan;

	private BufferedReader lastInvReader, currentInvReader;
	private BufferedWriter lastInvWriter, currentInvWriter;

    public InventoryManager() {
        productNames = new ArrayList<String>();
        inventoryNumbers = new ArrayList<Integer>();
		scan = new Scanner(System.in);

		try {
			lastInvReader = new BufferedReader(new FileReader(lastSavedInventoryFile));
			currentInvReader = new BufferedReader(new FileReader(currentInventoryFile));
			currentInvWriter = new BufferedWriter(new FileWriter(currentInventoryFile));

			makeCurrentInventory();

		} catch (FileNotFoundException e){
			System.out.println("File(s) not found in directory/IDE.");
		} catch (IOException e){
			System.out.println("File(s) cannot be written in.");
		}

    }
    
	public void makeCurrentInventory() {

		String line;
		try {		

			while((line = lastInvReader.readLine()) != null) {
				productNames.add(line.substring(0, line.indexOf(":")));
				inventoryNumbers.add(Integer.parseInt(line.substring(line.indexOf(":")+2)));

				currentInvWriter.write(productNames.get(productNames.size()-1) + ": " + inventoryNumbers.get(inventoryNumbers.size()-1) + System.lineSeparator());
			}
			lastInvReader.close();
			currentInvWriter.close();

		} catch (IOException e) {
			System.out.println("I/O Exception");
		}
		
	}

    
	public void run() {

		doStartPrompt();
		int input;
		try {
			while((input = scan.nextInt()) != 4) {
				switch(input) {
					case 1:
						System.out.println(commandSeparator);
						manageInventoryPrompt();
						doStartPrompt();
						break;
					case 2:
						System.out.println(commandSeparator);
						fulfillOrderPrompt();
							doStartPrompt();
						break;
					case 3:
						addDonationPrompt();
						doStartPrompt();
						break;
					default:
						System.out.println(commandSeparator);
						System.out.print("Invalid input; please provide me with a valid input : ");
						break;
				}
			}

			leaveTerminal();
			scan.close();

		} catch (InputMismatchException e) {
			System.out.println(commandSeparator + "\nInput was not a number. Restarting method...");
			scan.next();
			run();
		}
    }
    
	public void doStartPrompt(){
        System.out.print(commandSeparator + "\nWhat would you like to do?" + startPrompt);
	}

	
	public void manageInventoryPrompt() {

		System.out.print("Please select the desired tool:\n1. Show current inventory\n2. Edit product inventory\n3. Add received products\n4. Show target inventory\n5. Edit target inventory\n6. Save inventory\n> > > ");
		
		int input;
		try {

			while(scan.hasNextInt()) {

				switch(input = scan.nextInt()) {
					case 1:
						System.out.println(commandSeparator + "\n");
						showInventory();
						break;
					case 2:
						System.out.println(commandSeparator);
						System.out.println("NOTE: This operation requires MANAGER creditientials.\n");
						editProductInventory();
						break;
					case 3:
						System.out.println(commandSeparator); 
						addReceivedProducts();
						break;
					case 4:
						System.out.println(commandSeparator); 
						showTargetInventory();
						break;
					case 5:
						System.out.println(commandSeparator + "\nDo something 5"); 
						break;
					case 6:
						saveInventory(); 
						break;
					default:
						System.out.println(commandSeparator);
						System.out.print("Invalid input; please provide a proper option : ");
						break;
				}

				if(input >= 1 && input <= 6) {
					break;
				}

			}
		} catch (InputMismatchException e) {
			System.out.println(commandSeparator + "\nInput was not a number. Restarting method...");
			scan.next();
			manageInventoryPrompt();
		}
	}
	
	public void showInventory() {

		for(int i = 0; i < productNames.size(); i++) {
			System.out.print(productNames.get(i) + " - " + inventoryNumbers.get(i));
			System.out.print(" currently in stock \t || \t ");

			String stockStatus;
			int stockCount = inventoryNumbers.get(i);
			if(stockCount == 0) {
				stockStatus = "Product UNAVAILABLE";
			} else if(stockCount <= 700) {
				stockStatus = "Product in LOW range";
			} else if(stockCount <= 1150) {
				stockStatus = "Product in moderate range";
			} else {
				stockStatus = "Product in safe range";
			}

			System.out.println(" " + stockStatus);
		}
		System.out.println();
	}
	
	public void editProductInventory() {
		String username = "";
		String password = "";
		
		System.out.print("Please provide Manager's username : ");
		if(scan.hasNext()) {
			username = scan.next();
			System.out.print("Please provide Manager's password : ");
			if(scan.hasNext()) {
				password = scan.next();
			}	
		}
		
		if(!username.equals("manager") || !password.equals("password")) {
			System.out.println("Username and/or password incorrect.");
			editProductInventory();
		} else {
			String finalChoice = "";
			do {

				System.out.print("\nPlease provide the product SKU : ");
				int productSKU = scan.nextInt();
				System.out.print("Please provide the proper product operation [num to add or -num (neg. num) to remove] : ");
				int numAmount = scan.nextInt();

				ProductManager productAccess = new ProductManager();
				String chosenProduct = productAccess.getProductNameIdentifiers().get(productSKU);
				if(numAmount <= 0) {
					System.out.println("\nRemoving " + Math.abs(numAmount) +  " of " + chosenProduct + " from Inventory. Please confirm (y/n)");
				} else {
					System.out.println("\nAdding " + numAmount +  " of " + chosenProduct + " from Inventory. Please confirm (y/n)");
				}
				
				System.out.print("> > > ");

				finalChoice = scan.next();
				switch(finalChoice) {
					case "n":
						System.out.println("Operation will restart...\n");
						break;

					default:
						while(!finalChoice.equals("y") && !finalChoice.equals("n")) {	
							System.out.println("\nPlease provide a proper choice (y/n) : ");
							finalChoice = scan.next();

						}
						if(finalChoice.equals("y")) {

							int newStock = productAccess.getProductStockIdentifiers().get(productSKU) + numAmount;
							productAccess.getProductStockIdentifiers().put(productSKU, newStock);
							saveProductInventory(productAccess.getProductNameIdentifiers().get(productSKU), newStock);
							saveInventory();
						}
						break;
				}
			} while(finalChoice.equals("n"));
		}
	}

	public void addReceivedProducts() {
		
	}

	public void showTargetInventory() {
		
	}

	public void editTargetInventory() {

	}

	public void saveProductInventory(String productName, int newProductStock) {
		for(int i = 0; i < productNames.size(); i++) {
			if(productNames.get(i).equals(productName)) {
				inventoryNumbers.set(i, newProductStock);
				break;
			}
		}
	}
	
	public void saveInventory() {

		try {
			currentInvWriter = new BufferedWriter(new FileWriter(currentInventoryFile));

			for(int i = 0; i < productNames.size(); i++) {
				currentInvWriter.write(productNames.get(i) + ": " + inventoryNumbers.get(i) + System.lineSeparator());
			}
			currentInvWriter.close();

		} catch (IOException e) {
			System.out.println("Error trying to save inventory; unable to read/write from/in " + currentInventoryFile + ".");
		} finally {
			System.out.println("\nInventory has been updated.");
		}
		
	}
	

	public void fulfillOrderPrompt(){
		System.out.print("Please selected desired tool:\n1. Fulfill physical order\n2. Create online order\n3. Delete online order\n4. View online orders\n > > > ");
		
		int input;
		try {

			while(scan.hasNextInt()) {

				switch(input = scan.nextInt()) {
					case 1:
						System.out.println(commandSeparator + "\nDo something 1"); 
						break;
					case 2:
						System.out.println(commandSeparator + "\nDo something 2"); 
						break;
					case 3:
						System.out.println(commandSeparator + "\nDo something 3"); 
						break;
					case 4:
						System.out.println(commandSeparator + "\nDo something 4"); 
						break;
					default:
						if(input < 1 || input > 4) {
							System.out.println(commandSeparator);
							System.out.print("Invalid input; please provide a proper option : ");
						}
						break;
				}

				if(input >= 1 && input <= 4) {
					break;
				}
			}
		} catch (InputMismatchException e) {
			System.out.println(commandSeparator + "\nInput was not an integer. Restarting method...");
			scan.next();
			fulfillOrderPrompt();
		}
	}

	
	public void addDonationPrompt(){
		System.out.println(commandSeparator + "\nDo something about donation");
	}


	public void leaveTerminal() {
		System.out.print(commandSeparator + "\nPlease confirm leaving the system (y/n) > > > ");
		String input;
		try {

			while(scan.hasNext()) {

				switch(input = scan.next()) {
					case "y":
						endTask();
						break;
					case "n":
						run();
						break;
					default:
						System.out.print("Invalid input; please provide a proper option > > > ");
						break;
				}

				if(input.equals("y") || input.equals("n")) {
					break;
				}
			}
		} catch (InputMismatchException e) {
			System.out.println(commandSeparator + "\nInput was not a letter. Restarting method...");
			scan.next();
			leaveTerminal();
		}
	}

    public void endTask() {

		productNames.clear();
		inventoryNumbers.clear();

		String line;
		try {
			lastInvWriter = new BufferedWriter(new FileWriter(lastSavedInventoryFile));

			while((line = currentInvReader.readLine()) != null){
				productNames.add(line.substring(0, line.indexOf(":")));
				inventoryNumbers.add(Integer.parseInt(line.substring(line.indexOf(":")+2)));

				lastInvWriter.write(productNames.get(productNames.size()-1) + ": " + inventoryNumbers.get(inventoryNumbers.size()-1) + System.lineSeparator());
			}
			currentInvReader.close();
			lastInvWriter.close();

			System.out.println(commandSeparator + "\nInventory has been saved.");
			System.out.println("Ending terminal...");
		} catch (IOException e) {
			System.out.println("File(s) cannot be read by the program.");
		}
	}

	public ArrayList<String> getProductNames() {
		return productNames;
	}

	public ArrayList<Integer> getInventoryNumbers() {
		return inventoryNumbers;
	}
}
