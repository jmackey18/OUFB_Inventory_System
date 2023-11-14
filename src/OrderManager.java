import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class OrderManager {
    private LinkedList<OnlineOrder> incomingOrderQueue, outgoingOrderQueue, expiredOrderQueue, allOrdersQueue;

    private static final String LastOnlineOrderListFile = "OUFB_Inventory_System/txtfiles/LastOnlineOrderQueue.txt";
    private static final String CurrentOnlineOrderFile = "OUFB_Inventory_System/txtfiles/CurrentOnlineOrderQueue.txt";
    private BufferedReader lastOrderReader;
    private BufferedReader currentOrderReader;
    private Scanner scan;

    public OrderManager() {
        incomingOrderQueue = new LinkedList<OnlineOrder>();
        outgoingOrderQueue = new LinkedList<OnlineOrder>();
        expiredOrderQueue = new LinkedList<OnlineOrder>();
        allOrdersQueue = new LinkedList<OnlineOrder>();
        scan = new Scanner(System.in);

        try {
            lastOrderReader = new BufferedReader(new FileReader(LastOnlineOrderListFile));
            fillIncomingOrderQueue();
            findIncomingOrders();
            saveToCurrentOnlineOrderQueueList();
        
        } catch (FileNotFoundException e) {
            System.out.println("Unable to find OnlineOrderList.txt in system...");
        }
    }

    public void fillIncomingOrderQueue() {
        String line;
        try {
            String firstName = "";
            String lastName = "";
            String timeStamp = "";
            int newOrderProductSKU = 0;
            int newOrderProductAmount = 0;
            ArrayList<Order> newOrderList = null;
            HashSet<OnlineOrder> incomingTemp = new HashSet<>(incomingOrderQueue);

            while((line = lastOrderReader.readLine()) != null) {
                
                 if(line.contains("N:")) {
                    String[] fullName = line.substring(3).split(" ");
                    if(fullName.length > 2) {

                        StringJoiner firstNameJoiner = new StringJoiner(" ");
                        for(int i = 0; i < fullName.length-1; i++) {
                            firstNameJoiner.add(fullName[i]);
                        }
                        firstName = firstNameJoiner.toString();
                    
                    } else {
                        firstName = fullName[0];
                    }
                    lastName = fullName[fullName.length-1];
                
                } else if(line.contains("T:")) {
                    timeStamp = line.substring(3);
                
                } else if(line.contains("O: ")) {
                    line = line.substring(3);
                    if(line.contains(" | ")) {

                        newOrderList = new ArrayList<Order>();

                        String[] orderSplit = line.split(" \\| ");
                        for(String i : orderSplit) {

                            String[] split = i.split(", ");
                            newOrderList.add(new Order(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                        }
                    } else {
                        String[] split = line.split(", ");
                        newOrderProductSKU = Integer.parseInt(split[0]);
                        newOrderProductAmount = Integer.parseInt(split[1]);
                        newOrderList.add(new Order(newOrderProductSKU, newOrderProductAmount));
                    }
                }
                
                if(line.equals("")) {
                    if(!timeStamp.equals("")) {
                        if(incomingTemp.add(new OnlineOrder(new OrderList(newOrderList), firstName, lastName, timeStamp))) {
                            incomingOrderQueue.add(new OnlineOrder(new OrderList(newOrderList), firstName, lastName, timeStamp));
                        }
                        
                    }
                }
            }
            
            if(!timeStamp.equals("")) {
                if(incomingTemp.add(new OnlineOrder(new OrderList(newOrderList), firstName, lastName, timeStamp))) {
                    incomingOrderQueue.add(new OnlineOrder(new OrderList(newOrderList), firstName, lastName, timeStamp));
                }
                
            }

        } catch (IOException e) {
            System.out.println("Unable to read OnlineOrderList.txt in system...");
        }
    }

    public void findIncomingOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        HashSet<OnlineOrder> outgoingTemp = new HashSet<>(outgoingOrderQueue);
        HashSet<OnlineOrder> expiredTemp = new HashSet<>(expiredOrderQueue);

        for(OnlineOrder currentOrder : incomingOrderQueue) {

            if(currentOrder.getLocalDateTime().isAfter(currentTime.minusMinutes(90)) && currentOrder.getLocalDateTime().isBefore(currentTime.plusMinutes(90))) {
                if(outgoingTemp.add(currentOrder)) {
                    outgoingOrderQueue.add(currentOrder);
                }
                

            } else if(currentOrder.getLocalDateTime().isBefore(currentTime)) {
                if(expiredTemp.add(currentOrder)) {
                    expiredOrderQueue.add(currentOrder);
                }
            }
        }

        if(outgoingOrderQueue.size() != 0){
            for(OnlineOrder i : outgoingOrderQueue) {
                incomingOrderQueue.remove(i);
            }
        }

        if(expiredOrderQueue.size() != 0){
            for(OnlineOrder i : expiredOrderQueue) {
                incomingOrderQueue.remove(i);
            }
        }

        Collections.sort(incomingOrderQueue, new OrderTimeComparator());
        Collections.sort(outgoingOrderQueue, new OrderTimeComparator());
        Collections.sort(expiredOrderQueue, new OrderTimeComparator());
    }

    
    public void fulfillPhysicalOrder() {
        String finalChoice = "";
			do {

				ProductManager productAccess = new ProductManager();
                InventoryManager invAccess = new InventoryManager();
				int productSKU = InventoryManager.validatedSKU(productAccess);

				int numAmount = 0;
				while(true) {
					try {
						System.out.print("Please provide the desired quantity to take\n> > > ");
						numAmount = scan.nextInt();

						int currentInv = productAccess.getProductIdentifiers().get(productSKU).getProductStock();
						while(currentInv - numAmount < 0) {
							System.out.print("\nProduct operation must be less than/equal to " + currentInv + ". Please provide a valid quantity\n> > > ");
							numAmount = scan.nextInt();
						}

						break;
					} catch (InputMismatchException e) {
						System.out.println("\nInput must be a number.");
						scan.next();
					}
				}
				
				String chosenProduct = productAccess.getProductIdentifiers().get(productSKU).getProductName();
				System.out.println("\nRemoving " + numAmount +  " of " + chosenProduct + " from Inventory. Please confirm (y/n)");
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

							int newStock = productAccess.getProductIdentifiers().get(productSKU).getProductStock() - numAmount;
							Product oldProduct = productAccess.getProductIdentifiers().get(productSKU);
							Product newProduct = new Product(oldProduct.getProductName(), newStock, oldProduct.getProductTarget());
							
							productAccess.getProductIdentifiers().replace(productSKU, oldProduct, newProduct);
							
							invAccess.saveProductInventory(productAccess.getProductIdentifiers().get(productSKU).getProductName(), newStock);
							invAccess.saveInventory();

							System.out.print("\nWould you like to restart the operation? (yes/no)\n> > > ");
							finalChoice = scan.next();
							if(finalChoice.equals("yes")) {
								fulfillPhysicalOrder();
							} else if(finalChoice.equals("no")) {
								continue;
							}
						}
						break;
				}
		} while(finalChoice.equals("n"));
    }  

    
    public void createOnlineOrder() {

        System.out.print("Please enter customer's First Name below:\n> > > ");
        String firstName = scan.next();
        System.out.print("Please enter customer's Last Name below:\n> > > ");
        String lastName = scan.next();

        System.out.println("\nThe following will ask to put reservation date of order:");
            
        System.out.println("[yyyy-MM-dd] : 4-digit year - 2-digit month - 2-digit day (MUST BE IN EXACT DIGIT FORM; you can type in zero as a placeholder)");
        System.out.println("[T] : Placeholder for system; you MUST type T after date and before time (NO SPACES)");
        System.out.println("[HH:mm] : 2-digit hour : 2-digit minute (MUST BE IN EXACT DIGIT FORM; you can type in zero as a placeholder)");
        System.out.println("NOTE | Time is not based on 12-hour clock; time based on 24-hour clock");
        System.out.println("Example of valid input: \"2023-11-03T06:00\"");
        System.out.println("WARNING | NO EXTRA SPACES (should only be one line) | system WILL throw exception, which may affect other orders.");

        System.out.print("\nPlease enter the EXACT format of the reservation date [yyyy-MM-ddTHH:mm:ss]\n> > > ");
        String timeString = scan.next();

        while(!isValidDateTime(timeString)) {
            System.out.print("\nReservation date/time not the correct format. Please provide the valid format [yyyy-MM-ddTHH:mm:ss]\n> > > ");
            timeString = scan.next();
        }

        ArrayList<Order> orderList = new ArrayList<Order>();
        String finalChoice = "";
        do {
            ProductManager productAccess = new ProductManager();
            int skuInput = InventoryManager.validatedSKU(productAccess);

            int numAmount = 0;
            while(true) {
                try {
                    System.out.print("Please provide the desired quantity\n> > > ");
                    numAmount = scan.nextInt();

                    int currentInv = productAccess.getProductIdentifiers().get(skuInput).getProductStock();
                    while(currentInv - numAmount < 0) {
                        System.out.print("\nProduct operation must be less than/equal to " + currentInv + ". Please provide a valid quantity\n> > > ");
                        numAmount = scan.nextInt();
                    }

                    break;
                } catch (InputMismatchException e) {
                    System.out.println("\nInput must be a number.");
                    scan.next();
                }
            }

            Order newOrder = new Order(skuInput, numAmount);
            System.out.print("\n" + newOrder.easyToString() + ". Confirm (y/n)\n> > > ");

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

                        InventoryManager invAccess = new InventoryManager();
                        int newStock = productAccess.getProductIdentifiers().get(skuInput).getProductStock() - numAmount;
            			Product oldProduct = productAccess.getProductIdentifiers().get(skuInput);
            			Product newProduct = new Product(oldProduct.getProductName(), newStock, oldProduct.getProductTarget());
            					
            			productAccess.getProductIdentifiers().replace(skuInput, oldProduct, newProduct);
            					
            			invAccess.saveProductInventory(productAccess.getProductIdentifiers().get(skuInput).getProductName(), newStock);
            			invAccess.saveInventory();

                        orderList.add(newOrder);
                        System.out.print("\nWould you like to add another product? (yes/no)\n> > > ");
                                
                        finalChoice = scan.next();
                        while(!finalChoice.equals("yes") && !finalChoice.equals("no")) {
                            System.out.print("\nPlease provide a proper choice (yes/no)\n> > > ");
                            finalChoice = scan.next();
                        }
                        if(finalChoice.equals("yes")) {
                            finalChoice = "n";
                        } else if(orderList.size() == 1) {
                            orderList.add(new Order(100, 0));
                        } else {
                            continue;
                        }
                    }
                    break;
            }
        } while(finalChoice.equals("n"));

        OnlineOrder newOnlineOrder = new OnlineOrder(new OrderList(orderList), firstName, lastName, timeString);
        incomingOrderQueue.add(newOnlineOrder);
        Collections.sort(incomingOrderQueue, new OrderTimeComparator());
        saveToCurrentOnlineOrderQueueList();
        System.out.println("\nOnline order has been created; will be updated when system has been SAFELY exited.");
    }

    public boolean isValidDateTime(String inDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try {
            dateFormat.parse(inDate);
        } catch (DateTimeParseException pe) {
            return false;
        }
        return true;
    }

    
    public void deleteOnlineOrder() {
        boolean found = false;
        do {

            System.out.print("\nPlease enter customer's First Name below:\n> > > ");
            String firstName = scan.next();
            System.out.print("\nPlease enter customer's Last Name below:\n> > > ");
            String lastName = scan.next();
            
            OnlineOrder deletedOrder = null;

            for(OnlineOrder i : outgoingOrderQueue) {
                if(firstName.equals(i.getFirstName()) && lastName.equals(i.getLastName())) {
                    deletedOrder = i;
                    found = true;
                    break;
                }
            }

            if(deletedOrder == null) {
                for(OnlineOrder i : incomingOrderQueue) {
                    if(firstName.equals(i.getFirstName()) && lastName.equals(i.getLastName())) {
                        deletedOrder = i;
                        found = true;
                        break;
                    }
                }
            } else {
                outgoingOrderQueue.remove(deletedOrder);
                Collections.sort(outgoingOrderQueue, new OrderTimeComparator());

                restockDeletedInventory(deletedOrder);
            }
    
            if (!found) {
                System.out.println("\nOnline order was not found. First/Last name was incorrect.\n");
            } else {
                incomingOrderQueue.remove(deletedOrder);
                Collections.sort(incomingOrderQueue, new OrderTimeComparator());

                restockDeletedInventory(deletedOrder);
            }

        } while (!found);
        
        saveToCurrentOnlineOrderQueueList();
        System.out.println("\nOnline order was deleted.\n");
    }

    public void restockDeletedInventory(OnlineOrder deletedOrder) {
        ArrayList<Order> deletedOrderList = deletedOrder.getOrderList().getOrderList();

        for(Order order : deletedOrderList) {
            ProductManager productAccess = new ProductManager();

            if(order.getOrderProductSKU() != productAccess.getDefaultSku()) {
                int sku = order.getOrderProductSKU();
                int newProductInv = order.getOrderProductAmount() + productAccess.getProductIdentifiers().get(sku).getProductStock();
                    
                InventoryManager invAccess = new InventoryManager();
                invAccess.saveProductInventory(productAccess.getProductIdentifiers().get(sku).getProductName(), newProductInv);
            }
        }
    }
    
    public void saveToCurrentOnlineOrderQueueList() {
        allOrdersQueue.clear();
        allOrdersQueue.addAll(outgoingOrderQueue);
        allOrdersQueue.addAll(incomingOrderQueue);

        try (BufferedWriter orderWriter = new BufferedWriter(new FileWriter(CurrentOnlineOrderFile));) {
            
            for(int i = 0; i < allOrdersQueue.size(); i++) {
                orderWriter.write(allOrdersQueue.get(i).toString());
                if(i < allOrdersQueue.size()-1) orderWriter.newLine();
            }
        
        } catch (IOException e) {
            System.out.println("Unable to write in OnlineOrderList txt file...");
        }
    }
    
    public void saveToLastOnlineOrderQueueList() {
        String line;
        try(BufferedWriter lastOrderWriter = new BufferedWriter(new FileWriter(LastOnlineOrderListFile))) {
            currentOrderReader = new BufferedReader(new FileReader(CurrentOnlineOrderFile));

            while((line = currentOrderReader.readLine()) != null) {

                if(line.equals("")) { 
                    lastOrderWriter.write(System.lineSeparator());
                } else {
                    lastOrderWriter.write(line);
                        lastOrderWriter.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("System unable to read from CurrentOnlineOrderQueue.txt file...");
        }
    }
    
    
    public void viewOnlineorders() {
        OrderViewer newViewer = new OrderViewer();
        newViewer.waitForFrameClose();
    
        try {
            System.out.println("Leaving online orders...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }
    
    
    public LinkedList<OnlineOrder> getIncomingOrderQueue() {
        findIncomingOrders();
        return incomingOrderQueue;
    }

    public LinkedList<OnlineOrder> getOutgoingOrderQueue() {
        findIncomingOrders();
        return outgoingOrderQueue;
    }

    public LinkedList<OnlineOrder> getExpiredOrderQueue() {
        findIncomingOrders();
        return expiredOrderQueue;
    }

    public LinkedList<OnlineOrder> getAllOrdersQueue() {
        return allOrdersQueue;
    }  
}
