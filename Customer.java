import java.util.Random;

public class Customer implements Runnable{
	
	private int num;
	private int visits;
	private int balance;
	private int current_task;
	private int current_task_amount;
	private int loan_amount;
	private int current_employee;
	
	public Customer(int num, int visits, int balance){
		this.num = num;
		this.visits = visits;
		this.balance = balance;
		this.loan_amount = 0;
	}
	
	public int get_num(){
		return num;
	}
	
	public int get_balance() {
		return balance;
	}

	public void set_balance(int balance) {
		this.balance = balance;
	}
	
	public int get_current_task() {
		return current_task;
	}

	public int get_current_task_amount() {
		return current_task_amount;
	}

	public int get_loan_amount() {
		return loan_amount;
	}

	public void set_loan_amount(int loan_amount) {
		this.loan_amount = loan_amount;
	}

	public void set_current_employee(int current_employee) {
		this.current_employee = current_employee;
	}

	@Override
	public void run() {
		System.out.println("Customer " + this.num + " created");
		while(this.visits != 3){
			this.current_task = get_task();
			this.current_task_amount = get_task_amount();
	    	
	    	if(this.current_task == 0 || this.current_task == 1){
				try {
					// Customer waits in line
					BankSimulation.sem_teller_line.acquire();
					BankSimulation.teller_line.add(this);
					BankSimulation.sem_teller_line.release();
					
					// Customer is ready for teller
					BankSimulation.sem_ready_for_teller.release();
					
					// Customer waits until he has been assigned a teller
					BankSimulation.sem_employee_assigned[this.num].acquire();
					
					// Customer requests a task
					Thread.sleep(100);
					if(this.current_task == 0){
						System.out.println("Customer " + this.get_num() + " "
							+ "requests of teller " + this.current_employee + " to make a deposit of $" + this.get_current_task_amount());
					}
					else{
						System.out.println("Customer " + this.get_num() + " "
							+ "requests of teller " + this.current_employee + " to make a withdrawal of $" + this.get_current_task_amount());
					}
					BankSimulation.sem_customer_request_teller[this.current_employee].release(); // Signal the teller
					
					// Customer waits for teller to perform task
					BankSimulation.sem_perform_task[this.num].acquire();
					
					// Customer gets receipt
					Thread.sleep(100);
					if(this.current_task == 0){
						System.out.println("Customer " + this.num + " gets receipt from teller " + this.current_employee);
					}
					else{
						System.out.println("Customer " + this.num + " gets cash and receipt from teller " + this.current_employee);
					}
				} catch (InterruptedException e) {
					System.out.println("An error occurred.");
				}	
	    	}
	    	else if(this.current_task == 2){
				try {
					// Customer waits in line
					BankSimulation.sem_officer_line.acquire();
					BankSimulation.officer_line.add(this);
					BankSimulation.sem_officer_line.release();
					
					// Customer is ready for officer
					BankSimulation.sem_ready_for_officer.release();
					
					// Customer waits until he has been assigned an officer
					BankSimulation.sem_employee_assigned[this.num].acquire();
					
					// Customer requests a task
					Thread.sleep(100);
					System.out.println("Customer " + this.get_num() + " "
							+ "requests of loan officer to apply for a loan of $" + this.get_current_task_amount());
					BankSimulation.sem_customer_request_officer.release(); // Signal the officer
					
					// Customer waits for officer
					BankSimulation.sem_perform_task[this.num].acquire();
					
					// Customer gets receipt
					Thread.sleep(100);
					System.out.println("Customer " + this.num + " gets loan from loan officer " + this.current_employee);
					
				} catch (InterruptedException e) {
					System.out.println("An error occurred.");
				}	
	    	}
	    	
			this.visits++;
		}
		// Customer has done all his visits
		System.out.println("Customer " + this.num + " departs the bank");
	}

	// Get task for the customer (random)
	private int get_task() {
		Random r = new Random();
		return r.nextInt(3);
	}
	
	// Get amount of the task for the customer (random)
	private int get_task_amount() {
		Random r = new Random();

		return (r.nextInt(5) + 1) * 100;
	}
}
