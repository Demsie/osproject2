import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BankSimulation {

	public static Queue<Customer> teller_line = new LinkedList<>();
	public static Queue<Customer> officer_line = new LinkedList<>();
	public final static int NUM_OF_CUSTOMERS = 5;
	public final static int NUM_OF_TELLERS = 2;

	// Semaphores for Queue
	public static Semaphore sem_teller_line = new Semaphore(1, true);
	public static Semaphore sem_officer_line = new Semaphore(1, true);
	
	// Semaphores for signal for employee (customer ready)
	public static Semaphore sem_ready_for_teller = new Semaphore(0,true);
	public static Semaphore sem_ready_for_officer = new Semaphore(0,true);
	
	// Semaphores for each customer (task performed)
	public static Semaphore[] sem_perform_task = {new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true)};
	
	// Semaphores for each customer (employee assigned)
	public static Semaphore[] sem_employee_assigned = {new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true)};
	
	// Semaphores for each teller (customer made request)
	public static Semaphore[] sem_customer_request_teller = {new Semaphore(0, true), new Semaphore(0, true)};
	
	// Semaphore for officer (customer made request)
	public static Semaphore sem_customer_request_officer = new Semaphore(0, true);
	
	public static void main(String[] args) {

		//Creating threads for customers, tellers and officer
		Thread[] customers = new Thread[NUM_OF_CUSTOMERS];
		Customer[] customer_values = new Customer[NUM_OF_CUSTOMERS];
		for(int i=0; i < NUM_OF_CUSTOMERS; i++){
			Customer cust = new Customer(i,0,1000);
			customer_values[i] = cust;
			customers[i] = new Thread(cust);
			customers[i].start();
		}

		Thread[] tellers = new Thread[NUM_OF_TELLERS];
		for(int i=0; i < NUM_OF_TELLERS; i++){
			tellers[i] = new Thread(new Teller(i));
			tellers[i].start();
		}

		Thread loan_officer = new Thread(new LoanOfficer(0));
		loan_officer.start();

		//Join all threads
		for(int i=0; i < NUM_OF_CUSTOMERS; i++){
			try {
				customers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Customer " + i + " is joined by main");
		}
		
		// Summary
		System.out.println("\n\t\tBank Simulation Summary");
		System.out.println("\t\tEnding Balance\tLoan Amount");
		int total_balance = 0;
		int total_loan = 0;
		for(int i = 0; i< NUM_OF_CUSTOMERS; i++){
			total_balance += customer_values[i].get_balance();
			total_loan += customer_values[i].get_loan_amount();
			System.out.println("Customer " + i + "\t\t " + customer_values[i].get_balance()
					+ "\t\t " + customer_values[i].get_loan_amount());
		}
		System.out.println(" Totals\t\t\t " + total_balance + "\t\t " + total_loan);

		System.exit(0);
	}

}
