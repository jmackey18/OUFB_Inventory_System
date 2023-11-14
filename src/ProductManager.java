import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ProductManager {

    private InventoryManager invAccess;

    private ArrayList<String> productNames;
    private ArrayList<Integer> productSKUs, inventoryNumbers, invTargetNumbers;

    private HashMap<Integer, Product> productIdentifiers;
    
    private static int DEFAULT_SKU;

    public ProductManager() {
        invAccess = new InventoryManager();

		productSKUs = invAccess.getProductSKUs();
        productNames = invAccess.getProductNames();
        inventoryNumbers = invAccess.getInventoryNumbers();
        invTargetNumbers = invAccess.getInvTargetNumbers();

        productIdentifiers = new HashMap<Integer, Product>();
        DEFAULT_SKU = 100;

        productIdentifiers.putIfAbsent(DEFAULT_SKU, new Product("Default", -1, -1));
        for(int i = 0; i < productNames.size(); i++) {
            productIdentifiers.putIfAbsent(productSKUs.get(i), new Product(productNames.get(i), inventoryNumbers.get(i), invTargetNumbers.get(i)));
        }

    }

    public void addNewProduct(Employee currentEmp, Scanner scan) {
        String username = "";
		String password = "";
		
		System.out.print("Please provide Distributor/Manager's username\n> > > ");
        username = scan.next();
		if(username.equals("cancel")) return;
		System.out.print("Please provide Distributor/Manager's password\n> > > ");
		password = scan.next();
		if(password.equals("cancel")) return;
		
		if(!username.equals(currentEmp.getUserName()) || !password.equals(currentEmp.getPassword())) {
			System.out.println("Username and/or password incorrect.");
			addNewProduct(currentEmp, scan);

		} else {
			String finalChoice = "";
			do {

				int productSKU = InventoryManager.validatedSKU(this);
				
				String name = "";
				int numAmount = 0;
				int targetAmt = 0;
				while(true) {
					try {
						scan.nextLine();
						do {
							System.out.println("Please provide the product name below: ");
							name = scan.nextLine();
						} while(name.isEmpty());

						System.out.print("Please provide the current stock of the product\n> > > ");
						numAmount = scan.nextInt();

						while(numAmount < 0) {
							System.out.print("Current inventory cannot be negative. Please provide a valid minimum\n> > > ");
							numAmount = scan.nextInt();
						}

						System.out.print("Please provide the desired target stock of the product\n> > > ");
						targetAmt = scan.nextInt();

						while(numAmount < 0) {
							System.out.print("Target inventory cannot be negative. Please provide a valid minimum\n> > > ");
							targetAmt = scan.nextInt();
						}

						break;
					} catch (InputMismatchException e) {
						System.out.println("\nInput must be a number.");
						scan.next();
					}
				}

				System.out.println("\nDescription of new product. Please confirm (y/n):");
				System.out.println("Product SKU #: " + productSKU);
				System.out.println("Product Name: " + name);
				System.out.println("Current inventory: " + numAmount);
				System.out.println("Target inventory: " + targetAmt);
				System.out.print("> > > ");

				finalChoice = scan.next();
				switch(finalChoice) {
					case "n":
						System.out.println("Operation will restart...\n");
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {}
						break;

					default:
						while(!finalChoice.equals("y") && !finalChoice.equals("n")) {	
							System.out.print("\nPlease provide a proper choice (y/n)\n> > > ");
							finalChoice = scan.next();

						}
						if(finalChoice.equals("y")) {
							
							productIdentifiers.putIfAbsent(productSKU, new Product(name, numAmount, targetAmt));

							productSKUs.add(productSKU);
							productNames.add(name);
							inventoryNumbers.add(numAmount);
							invTargetNumbers.add(targetAmt);

                            System.out.println("\nNew product added to inventory system.");

						}
						break;
				}
			} while(finalChoice.equals("n"));
		}
    }

    public void deleteProduct(Employee currentEmp, Scanner scan) {
        String username = "";
		String password = "";
		
		System.out.print("Please provide Distributor/Manager's username\n> > > ");
		username = scan.next();
		if(username.equals("cancel")) return;

		System.out.print("Please provide Distributor/Manager's password\n> > > ");
		password = scan.next();
		if(password.equals("cancel")) return;
		
		if(!username.equals(currentEmp.getUserName()) || !password.equals(currentEmp.getPassword())) {
			System.out.println("Username and/or password incorrect.");
			deleteProduct(currentEmp, scan);

		} else {
			String finalChoice = "";
			do {
				int productSKU = InventoryManager.validatedSKU(this);
				
				String name = productIdentifiers.get(productSKU).getProductName();
				int numAmount = productIdentifiers.get(productSKU).getProductStock();
				int targetAmt = productIdentifiers.get(productSKU).getProductTarget();

				System.out.println("\nDescription of product. Please confirm (y/n):");
				System.out.println("Product SKU #: " + productSKU);
				System.out.println("Product Name: " + name);
				System.out.println("Current inventory: " + numAmount);
				System.out.println("Target inventory: " + targetAmt);
				System.out.print("> > > ");

				finalChoice = scan.next();
				switch(finalChoice) {
					case "n":
						System.out.println("Operation will restart...\n");
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {}
						break;

					default:
						while(!finalChoice.equals("y") && !finalChoice.equals("n")) {	
							System.out.print("\nPlease provide a proper choice (y/n)\n> > > ");
							finalChoice = scan.next();

						}
						if(finalChoice.equals("y")) {
							
							productIdentifiers.remove(productSKU);

							for(int i = 0; i < productNames.size(); i++) {
								if(productNames.get(i).equals(name)) {
									
									productSKUs.remove(i);
									productNames.remove(i);
									inventoryNumbers.remove(i);
									invTargetNumbers.remove(i);
									break;
								}
							}
						}
						break;
				}
			} while(finalChoice.equals("n"));
		}
    }

    /**
     * Getter method for ProductManager. Retrieves a list of Products' SKU numbers & 
     * their descriptions. Typically useful when needing to find/retrieve a desired Product.
     * @return (HashMap, Key: int | Value: Product) A list of each Product's SKU number & their elements.
     */
    public HashMap<Integer, Product> getProductIdentifiers() {
        return productIdentifiers;
    }

	/**
     * Getter method for ProductManager. Retrieves a list of Products' SKU numbers. 
     * Typically useful when adding, editing, or deleting a Product.
     * @return  (ArrayList of Ints) A list of each Product's SKU numbers in OUFB inventory system.
     */
	public ArrayList<Integer> getProductSKUs() {
		return productSKUs;
	}

    /**
     * Getter method for ProductManager. Retrieves a list of Products' target (minimum) 
     * stocks. Typically useful when adding, editing, or deleting a Product.
     * @return  (ArrayList of Ints) A list of each Product's current stock in OUFB inventory system.
     */
    public ArrayList<String> getProductNames() {
        return productNames;
    }

    /**
     * Getter method for ProductManager. Retrieves a list of Products' target (minimum) 
     * stocks. Typically useful when adding, editing, or deleting a Product.
     * @return  (ArrayList of Ints) A list of each Product's current stock in OUFB inventory system.
     */
    public ArrayList<Integer> getInventoryNumbers() {
        return inventoryNumbers;
    }

    /**
     * Getter method for ProductManager. Retrives a list of Products' target (minimum) 
     * stocks. Typically useful when adding, editing, or deleting a Product.
     * @return (ArrayList of Ints) A list of each Product's target stock in OUFB inventory system.
     */
    public ArrayList<Integer> getInvTargetNumbers() {
        return invTargetNumbers;
    }

    /**
     * Getter method for ProductManager. Retrieves a Product's SKU number.
     * Typically useful when retrieving productIdentifiers's key.
     * @return (Int) SKU number of a desired Product.
     */
    public int getDefaultSku() {
        return DEFAULT_SKU;
    }
}
