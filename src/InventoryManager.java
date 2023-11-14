import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.JFrame;

public class InventoryManager {
	
    private final String lastSavedInventoryFile = "OUFB_Inventory_System/txtfiles/LastSavedInventory.txt";
	private final String currentInventoryFile = "OUFB_Inventory_System/txtfiles/CurrentInventory.txt";
	private final String inventoryTargetFile = "OUFB_Inventory_System/txtfiles/InventoryTarget.txt";
    
	private final String commandSeparator = "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
    private final String startPrompt = "\n1. Manage Inventory\n2. Fulfill an order\n3. Add a donation\n4. Leave terminal\n> > > ";
	
	private static ArrayList<String> productNames, stockStatusList;
    private static ArrayList<Integer> productSKUs, inventoryNumbers, invTargetNumbers;
	private EmpList emplistAccess;
	private Employee currentEmp;
	private static OrderManager orderAccess;
	private static Scanner scan;

	private BufferedReader lastInvReader, currentInvReader, invTargetReader;
	private BufferedWriter lastInvWriter, currentInvWriter, invTargetWriter;

    public InventoryManager() {
		productSKUs = new ArrayList<Integer>();
        productNames = new ArrayList<String>();
        inventoryNumbers = new ArrayList<Integer>();
		invTargetNumbers = new ArrayList<Integer>();
		stockStatusList = new ArrayList<String>();

		emplistAccess = new EmpList();
		orderAccess = new OrderManager();
		currentEmp = null;
		scan = new Scanner(System.in);

		try {
			lastInvReader = new BufferedReader(new FileReader(lastSavedInventoryFile));
			currentInvReader = new BufferedReader(new FileReader(currentInventoryFile));
			currentInvWriter = new BufferedWriter(new FileWriter(currentInventoryFile));
			invTargetReader = new BufferedReader(new FileReader(inventoryTargetFile));

			makeCurrentInventory();

		} catch (FileNotFoundException e){
			System.out.println("File(s) not found in directory/IDE.");
		} catch (IOException e){
			System.out.println("File(s) cannot be written in.");
		}


    }
    
	public void makeCurrentInventory() {

		String line;
		String targetLine;
		try {		

			while((line = lastInvReader.readLine()) != null) {
				productSKUs.add(Integer.parseInt(line.substring(0, line.indexOf(","))));
				productNames.add(line.substring(line.indexOf(",")+1, line.indexOf(":")));
				inventoryNumbers.add(Integer.parseInt(line.substring(line.indexOf(":")+2)));

				currentInvWriter.write(productSKUs.get(productSKUs.size()-1) + "," + productNames.get(productNames.size()-1) + ": " + inventoryNumbers.get(inventoryNumbers.size()-1) + System.lineSeparator());
			}
			lastInvReader.close();
			currentInvWriter.close();

			while((targetLine = invTargetReader.readLine()) != null) {
			invTargetNumbers.add(Integer.parseInt(targetLine.substring(targetLine.indexOf(":")+2)));
			}
			invTargetReader.close();

			for(int i = 0; i < productNames.size(); i++) {
				int currentStock = inventoryNumbers.get(i);
				int targetStock = invTargetNumbers.get(i);

				String status = "";
				if(currentStock == 0) {
					status = "UNAVAILABLE";
				} else if(currentStock <= targetStock) {
					status = "RUNNING LOW";
				} else if(currentStock > targetStock && currentStock <= targetStock*2) {
					status = "Moderate";
				} else {
					status = "Available";
				}

				stockStatusList.add(status);
			}

		} catch (IOException e) {
			System.out.println("I/O Exception");
		}
	}

    
	public void run() {
		boolean invalidCreds = true;
		do {
			System.out.println(commandSeparator+"\n");
			System.out.print("Please enter your username > > > ");
			String userInput = scan.next();
			System.out.print("Please enter your password > > > ");
			String passInput = scan.next();

			for(String emp : emplistAccess.getAllCreds().keySet()) {
				Employee i = emplistAccess.getAllCreds().get(emp);

				if(i.equals(new Employee(userInput, passInput, i.getPosition()))) {
					System.out.println("\nEntering the OU Food Bank Inventory System. Welcome, " + emp + "!");
					currentEmp = new Employee(userInput, passInput, i.getPosition());
					invalidCreds = false;
					break;
				}
			}
			if(invalidCreds) {
				System.out.println("\nInvalid username/password.");
			}
		} while(invalidCreds);

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

		System.out.print("Please select the desired tool:\n1. Show all inventory\n2. Edit product inventory\n3. Edit target inventory\n4. Add new product\n5. Delete a product\n6. Save inventory\n7. Cancel\n> > > ");
		
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
						System.out.println("NOTE: This operation requires DISTRIBUTION member or MANAGER credentials.\n"); 
						if(currentEmp.getPosition().equals("Manager") || currentEmp.getPosition().equals("Distributor")) {
							editProductInventory();
						} else {
							System.out.println("You are not a Distributor/Manager; restarting prompt...");
							System.out.println(commandSeparator);
							manageInventoryPrompt();
						}
						break;

					case 3:
						System.out.println(commandSeparator);
						System.out.println("NOTE: This operation requires DISTRIBUTION member or MANAGER credentials.\n"); 
						if(currentEmp.getPosition().equals("Manager") || currentEmp.getPosition().equals("Distributor")) {
							editTargetInventory();
						} else {
							System.out.println("You are not a Distributor/Manager; restarting prompt...");
							Thread.sleep(1500);
							System.out.println(commandSeparator);
							manageInventoryPrompt();
						}
						break;

					case 4:
						System.out.println(commandSeparator);
						System.out.println("NOTE: This operation requires DISTRIBUTION member or MANAGER credentials.\n"); 
						if(currentEmp.getPosition().equals("Manager") || currentEmp.getPosition().equals("Distributor")) {
							addNewProduct();
						} else {
							System.out.println("You are not a Distributor/Manager; restarting prompt...");
							Thread.sleep(1500);
							System.out.println(commandSeparator);
							manageInventoryPrompt();
						}
						break;
					
					case 5:
						System.out.println(commandSeparator);
						System.out.println("NOTE: This operation requires DISTRIBUTION member or MANAGER credentials.\n"); 
						if(currentEmp.getPosition().equals("Manager") || currentEmp.getPosition().equals("Distributor")) {
							deleteProduct();
						} else {
							System.out.println("You are not a Distributor/Manager; restarting prompt...");
							Thread.sleep(1500);
							System.out.println(commandSeparator);
							manageInventoryPrompt();
						}
						break;

					case 6:
						saveInventory(); 
						break;

					case 7:
						System.out.println(commandSeparator);
						System.out.println("Proceeding to previous prompt...");
						break;

					default:
						System.out.println(commandSeparator);
						System.out.print("Invalid input; please provide a proper option : ");
						break;
				}

				if(input >= 1 && input <= 7) {
					break;
				}

			}
		} catch (InputMismatchException e) {
			System.out.println(commandSeparator + "\nInput was not a number. Restarting method...");
			scan.next();
			manageInventoryPrompt();
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Utilizes InvStockViewer class to display all of inventory system's Products' name, 
	 * current stock, target (minimum) stock, and stock statuses.
	 */
	public void showInventory() {

		InvStockViewer newViewer = new InvStockViewer();
		newViewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        newViewer.pack();
        newViewer.setVisible(true);

		System.out.println("Showing inventory...");
		newViewer.waitForFrameClose();

		try {
			System.out.println("Leaving inventory...");
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

	}
	
	public void editProductInventory() {
		String username = "";
		String password = "";
		
		System.out.print("Please provide Distributor/Manager's username\n> > > ");
		username = scan.next();
		System.out.print("Please provide Distributor/Manager's password\n> > > ");
		password = scan.next();
		
		if(username.equals("cancel") || password.equals("cancel")) return;

		if(!username.equals(currentEmp.getUserName()) || !password.equals(currentEmp.getPassword())) {
			System.out.println("Username and/or password incorrect.");
			editProductInventory();

		} else {
			String finalChoice = "";
			do {

				ProductManager productAccess = new ProductManager();
				int productSKU = validatedSKU(productAccess);

				int numAmount = 0;
				while(true) {
					try {
						System.out.print("Please provide the proper product operation [num to add or -num (neg. num) to remove]\n> > > ");
						numAmount = scan.nextInt();

						int currentInv = productAccess.getProductIdentifiers().get(productSKU).getProductStock();
						while(numAmount + currentInv < 0) {
							System.out.print("\nProduct operation must be less than/equal to " + currentInv + ". Please provide a valid operation\n> > > ");
							numAmount = scan.nextInt();
						}

						break;
					} catch (InputMismatchException e) {
						System.out.println("\nInput must be a number.");
						scan.next();
					}
				}
				
				String chosenProduct = productAccess.getProductIdentifiers().get(productSKU).getProductName();
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
							System.out.println("\nPlease provide a proper choice (y/n)\n> > > ");
							finalChoice = scan.next();

						}
						if(finalChoice.equals("y")) {

							int newStock = productAccess.getProductIdentifiers().get(productSKU).getProductStock() + numAmount;
							Product oldProduct = productAccess.getProductIdentifiers().get(productSKU);
							Product newProduct = new Product(oldProduct.getProductName(), newStock, oldProduct.getProductTarget());
							
							productAccess.getProductIdentifiers().replace(productSKU, oldProduct, newProduct);
							
							
							saveProductInventory(productAccess.getProductIdentifiers().get(productSKU).getProductName(), newStock);
							saveInventory();

							System.out.print("\nWould you like to restart the operation? (yes/no)\n> > > ");
							finalChoice = scan.next();
							if(finalChoice.equals("yes")) {
								editProductInventory();
							} else if(finalChoice.equals("no")) {
								continue;
							}
						}
						break;
				}
			} while(finalChoice.equals("n"));
		}
	}

	/**
	 * First request authorized credentials to proceed with changing inventory system's data, particularly 
	 * the system's list of target (minimum) stocks. Method then requests an existing Product SKU number and 
	 * valid target stock. Method verifies given information then effectively saves desired Product's target 
	 * stock. Utilizes InventoryManager's saveTargetInventory(String, int) method.
	 */
	public void editTargetInventory() {
		String username = "";
		String password = "";
		
		System.out.print("Please provide Distributor/Manager's username (\"cancel\" to cancel)\n> > > ");
		username = scan.next();
		System.out.print("Please provide Distributor/Manager's password (\"cancel\" to cancel)\n> > > ");
		password = scan.next();
		
		if(username.equals("cancel") || password.equals("cancel")) return;

		if(!username.equals(currentEmp.getUserName()) || !password.equals(currentEmp.getPassword())) {
			System.out.println("Username and/or password incorrect.");
			editTargetInventory();

		} else {
			String finalChoice = "";
			do {

				ProductManager productAccess = new ProductManager();
				int productSKU = validatedSKU(productAccess);
				
				int numAmount = 0;
				while(true) {
					try {
						System.out.print("Please provide the desired target (minimum) stock of the product\n> > > ");
						numAmount = scan.nextInt();

						while(numAmount < 0) {
							System.out.print("Target inventory cannot be negative. Please provide a valid minimum\n> > > ");
							numAmount = scan.nextInt();
						}

						break;
					} catch (InputMismatchException e) {
						System.out.println("\nInput must be a number.");
						scan.next();
					}
				}

				String chosenProduct = productAccess.getProductIdentifiers().get(productSKU).getProductName();
				System.out.println("\nThe target inventory of " + chosenProduct + " will be " + numAmount + ". Please confirm (y/n)");
				System.out.print("> > > ");

				finalChoice = scan.next();
				switch(finalChoice) {
					case "n":
						System.out.println("Operation will restart...\n");
						break;
					default:
						while(!finalChoice.equals("y") && !finalChoice.equals("n")) {	
							System.out.print("\nPlease provide a proper choice (y/n)\n> > > ");
							finalChoice = scan.next();

						}
						if(finalChoice.equals("y")) {

							int newTarget = numAmount;

							Product oldProduct = productAccess.getProductIdentifiers().get(productSKU);
							Product newProduct = new Product(oldProduct.getProductName(), oldProduct.getProductStock(), newTarget);
							
							productAccess.getProductIdentifiers().replace(productSKU, oldProduct, newProduct);

							saveTargetInventory(productAccess.getProductIdentifiers().get(productSKU).getProductName(), newTarget);
							saveTargetInventory();
						}
						break;
				}
			} while(finalChoice.equals("n"));
		}
	}

	/**
	 * Helper method for InventoryManager. Saves updated version of a Product's current stock. 
	 * Typically useful when editing a Product's current stock.
	 * 
	 * @param productName	(String) The name of the edited Product.
	 * @param newProductStock	(Int) The updated stock amount of the desired Product.
	 */
	public void saveProductInventory(String productName, int newProductStock) {
		for(int i = 0; i < productNames.size(); i++) {
			if(productNames.get(i).equals(productName)) {
				inventoryNumbers.set(i, newProductStock);
				break;
			}
		}
	}

	/**
	 * Helper method for InventoryManager. Saves updated version of a Product's target (minimum) 
	 * stock. Typically useful when editing a Product's target stock. Similar to InventoryManager's 
	 * saveProductInventory(String, int) method.
	 * 
	 * @param productName	(String) The name of the edited Product.
	 * @param newTarget		(Int) The edited target stock.
	 */
	public void saveTargetInventory(String productName, int newTarget) {
		for(int i = 0; i < productNames.size(); i++) {
			if(productNames.get(i).equals(productName)) {
				invTargetNumbers.set(i, newTarget);
				break;
			}
		}
	}
	
	/**
	 * Helper method for InventoryManager to gather concluding data from ProductManager's unique 
	 * addNewProduct() method. Retrieves updated lists of Product SKU numbers, names, their current stock, and 
	 * their target (minimum) stock. Method utilizes InventoryManager's saveInventory(), saveTargetInventory(), 
	 * and saveToLastSavedInventory() methods to effectively update and save list of products in system.
	 */
	public void addNewProduct() {
		ProductManager productAccess = new ProductManager();
		productAccess.addNewProduct(currentEmp, scan);

		productSKUs = productAccess.getProductSKUs();
		productNames = productAccess.getProductNames();
		invTargetNumbers = productAccess.getInvTargetNumbers();
		inventoryNumbers = productAccess.getInventoryNumbers();

		saveInventory();
		saveTargetInventory();
		saveToLastSavedInventory();
	}

	/**
	 * Helper method for InventoryManager to gather concluding data from ProductManager's unique 
	 * deleteProduct() method. Retrieves updated lists of Product SKU numbers, names, their current stock, and 
	 * their target (minimum) stock. Method utilizes InventoryManager's saveInventory(), saveTargetInventory(), 
	 * and saveToLastSavedInventory() methods to effectively update and save list of products in system.
	 */
	public void deleteProduct() {
		ProductManager productAccess = new ProductManager();
		productAccess.deleteProduct(currentEmp, scan);

		productSKUs = productAccess.getProductSKUs();
		productNames = productAccess.getProductNames();
		invTargetNumbers = productAccess.getInvTargetNumbers();
		inventoryNumbers = productAccess.getInventoryNumbers();

		saveInventory();
		saveTargetInventory();
		saveToLastSavedInventory();
	}

	/**
	 * After adding, deleting, or editing a Product's current stock or Online Order, method updates 
	 * CurrentInventory.txt. Will throw IOException if file is unable to be written in
	 */
	public void saveInventory() {
		try {
			currentInvWriter = new BufferedWriter(new FileWriter(currentInventoryFile));

			for(int i = 0; i < productNames.size(); i++) {
				currentInvWriter.write(productSKUs.get(i) + "," + productNames.get(i) + ": " + inventoryNumbers.get(i) + System.lineSeparator());
			}
			currentInvWriter.close();

		} catch (IOException e) {
			System.out.println("Error trying to save inventory; unable to write in " + currentInventoryFile + ".");
		} finally {
			System.out.println("\nInventory has been updated.");
		}
		
	}

	/**
	 * After adding, deleting, or editing a Product's target (minimum) stock, method updates 
	 * TargetInventory.txt. Will throw IOException if file is unable to be written in.
	 */
	public void saveTargetInventory() {
		try {
			invTargetWriter = new BufferedWriter(new FileWriter(inventoryTargetFile));

			for(int i = 0; i < productNames.size(); i++) {
				invTargetWriter.write(productNames.get(i) + ": " + invTargetNumbers.get(i) + System.lineSeparator());
			}
			invTargetWriter.close();

		} catch (IOException e) {
			System.out.println("Error trying to save target inventory; unable to write in " + inventoryTargetFile + ".");
		} finally {
			System.out.println("\nTarget inventory has been updated.");
		}
	}

	/**
	 * Helper method to InventoryManager. Reads CurrentInventory.txt in order to write each line 
	 * to LastSavedInventory.txt. Releases statements either verifying that inventory was saved & ending 
	 * session or warning that inventory unable to save via IOException.
	 */
	public void saveToLastSavedInventory() {
		String line;
		try {
			lastInvWriter = new BufferedWriter(new FileWriter(lastSavedInventoryFile));
			currentInvReader = new BufferedReader(new FileReader(currentInventoryFile));

			while((line = currentInvReader.readLine()) != null) {
				lastInvWriter.write(line + System.lineSeparator());
				}
			currentInvReader.close();
			lastInvWriter.close();

		} catch (IOException e) {
			System.out.println("File(s) cannot be read by the program.");
		}
	}
	
	/**
	 * Second prompt to InventoryManager. Tools include fulfilling an in-store order, creating an 
	 * online order, deleting an online order, and viewing a list of current online orders.
	 */
	public void fulfillOrderPrompt(){
		orderAccess.saveToLastOnlineOrderQueueList();
		System.out.print("Please selected desired tool:\n1. Fulfill physical order\n2. Create online order\n3. Delete online order\n4. View online orders\n5. Cancel\n> > > ");
		
		int input;
		try {
			while(scan.hasNextInt()) {

				switch(input = scan.nextInt()) {
					case 1:
						System.out.println(commandSeparator + "\n"); 
						orderAccess.fulfillPhysicalOrder();
						break;
					case 2:
						System.out.println(commandSeparator + "\n"); 
						orderAccess.createOnlineOrder();
						break;
					case 3:
						System.out.println(commandSeparator + "\n"); 
						orderAccess.deleteOnlineOrder();
						break;
					case 4:
						System.out.println(commandSeparator+"\n");
						orderAccess.viewOnlineorders();
						break;
					case 5:
						System.out.println(commandSeparator+"\n");
						System.out.println("Proceeding to previous prompt...");
						break;
					default:
						if(input < 1 || input > 5) {
							System.out.println(commandSeparator);
							System.out.print("Invalid input; please provide a proper option : ");
						}
						break;
				}

				if(input >= 1 && input <= 5) {
					break;
				}
			}
		} catch (InputMismatchException e) {
			System.out.println(commandSeparator + "\nInput was not an integer. Restarting method...");
			scan.next();
			fulfillOrderPrompt();
		}
	}

	
	/**
	 * Donation prompt to InventoryManager. Similar to editProductInventory, method requests an 
	 * existing Product SKU number and valid donation input from user. Method will verify information 
	 * of donation. Method capable of detecting mismatched as well as invalid input.
	 */
	public void addDonationPrompt(){
		String finalChoice = "";
		do {

		ProductManager productAccess = new ProductManager();
		int productSKU = validatedSKU(productAccess);

		int numAmount = 0;
		while(true) {
			try {
				System.out.print("\nPlease provide donation amount\n> > > ");
				numAmount = scan.nextInt();

				int currentInv = productAccess.getProductIdentifiers().get(productSKU).getProductStock();
				while(numAmount + currentInv < 0 || numAmount + currentInv < currentInv) {
					System.out.print("\nOperation must be ADDING ONTO current product stock. Please provide a valid donation.\n> > > ");
						numAmount = scan.nextInt();
				}

				break;
			} catch (InputMismatchException e) {
				System.out.println("\nInput must be a number.");
				scan.next();
			}
		}
				
		String chosenProduct = productAccess.getProductIdentifiers().get(productSKU).getProductName();
		System.out.println("\nAdding " + numAmount +  " of " + chosenProduct + " from Inventory. Please confirm (y/n)");
				
		System.out.print("> > > ");

		finalChoice = scan.next();
		switch(finalChoice) {
			case "n":
				System.out.println("Operation will restart...\n");
				break;

			default:
				while(!finalChoice.equals("y") && !finalChoice.equals("n")) {	
					System.out.println("\nPlease provide a proper choice (y/n)\n> > > ");
					finalChoice = scan.next();

				}
				if(finalChoice.equals("y")) {

					int newStock = productAccess.getProductIdentifiers().get(productSKU).getProductStock() + numAmount;
					Product oldProduct = productAccess.getProductIdentifiers().get(productSKU);
					Product newProduct = new Product(oldProduct.getProductName(), newStock, oldProduct.getProductTarget());
							
					productAccess.getProductIdentifiers().replace(productSKU, oldProduct, newProduct);
							
					saveProductInventory(productAccess.getProductIdentifiers().get(productSKU).getProductName(), newStock);
					saveInventory();
					System.out.print("\nWould you like to add another donation? (yes/no)\n> > > ");
					
					finalChoice = scan.next();
					if(finalChoice.equals("yes")) {
						addDonationPrompt();
					} else if(finalChoice.equals("no")) {
						System.out.println("\nThank you for the donation!");
						continue;
					}
				}
				break;
			}
		} while(finalChoice.equals("n"));	
	}

	/**
	 * Confirmation procedure in InventoryManager to verify user-input to end current session. If yes, 
	 * system will route to endTask() method. If no, run() will proceed. Method will repeat until
	 * "y" or "n" has been inputted.
	 */
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

    /**
	 * Last procedure/method for OUFB inventory system. Saves all outgoing & incoming online orders to 
	 * LastOnlineOrderQueue.txt. Uses saveToLastSavedInventory() method.
	 */
	public void endTask() {
		orderAccess.saveToLastOnlineOrderQueueList();

		saveToLastSavedInventory();
		System.out.println("Inventory saved. Ending session...");
	}

	
	/**
	 * Helper method to validate a user-input SKU for OUFB inventory system. Method allows user to input either
	 * an existing or unique SKU number (ranging from 101 to 999) in order to locate or create a Product, respectively.
	 * 
	 * @param productAccess Current data of ProductManager.
	 * @return Either an existing or unique SKU number (Int).
	 */
	public static int validatedSKU(ProductManager productAccess) {
		int productSKU = 0;
		boolean invalidInput = true;
		boolean validSKU = false;
		while(invalidInput) {
			try {
				
				System.out.print("\nPlease provide the product SKU\n> > > ");
				productSKU = scan.nextInt();	
				boolean addProduct = false;	
					
				for(int sku : productAccess.getProductIdentifiers().keySet()) {
					if(productSKU == sku && productSKU != 100) {
						validSKU = true;
						break;
					}
				}
	
				if(validSKU == false) {
					System.out.print("\nProduct SKU does not exist. Are you making a new product? (y/n)\n> > > ");
					String choice = scan.next();
					boolean verify = false;
					
					do {
						switch(choice) {
							case "n":
								System.out.println("Operation will restart...\n");
								Thread.sleep(1500);
								verify = true;
								break;

							case "y":
								verify = true;
								System.out.println("\nMaking a new product SKU...");
								Thread.sleep(1500);

								if(productSKU != 100 && productSKU < 1000) {
									addProduct = true;
									validSKU = true;
								} else {
									System.out.println("SKU is not an appropriate ID. Operation will restart...\n");
									Thread.sleep(1500);
								}
								break;

							default:
								while(!choice.equals("y") && !choice.equals("n")) {	
									System.out.print("\nPlease provide a proper choice (y/n)\n> > > ");
									choice = scan.next();
								}
								break;
						}
					} while(!verify);
				}

				if(validSKU || addProduct) {
					invalidInput = false;
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInput was not a number; you must enter a proper, numbered SKU.");
				scan.next();
			} catch (InterruptedException e) {}
		}
		
		return productSKU;
	}

	
	/**
	 * Getter method for InventoryManager's productSKUs ArrayList.
	 * @return An ArrayList of current Product SKU numbers in inventory system (Int).
	 */
	public ArrayList<Integer> getProductSKUs() {
		return productSKUs;
	}

	/**
	 * Getter method for InventoryManager's productNames ArrayList.
	 * @return An ArrayList of current Product names in inventory system (String).
	 */
	public ArrayList<String> getProductNames() {
		return productNames;
	}

	/**
	 * Getter method for InventoryManager's inventoryNumbers ArrayList.
	 * @return An ArrayList of current Products' stock in inventory system (Int).
	 */
	public ArrayList<Integer> getInventoryNumbers() {
		return inventoryNumbers;
	}

	/**
	 * Getter method for InventoryManager's invTargetNumbers ArrayList.
	 * @return An ArrayList of current Products' target (minimum) stock in inventory system (Int).
	 */
	public ArrayList<Integer> getInvTargetNumbers() {
		return invTargetNumbers;
	}

	/**
	 * Getter method for InventoryManager's stockStatusList ArrayList.
	 * @return An ArrayList of current Products' stock statuses in inventory system (String).
	 */
	public ArrayList<String> getStockStatusList() {
		return stockStatusList;
	}
}
