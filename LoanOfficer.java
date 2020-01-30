
public class LoanOfficer implements Runnable {
	
	private int officer_number;
	
	public LoanOfficer(int i) {
		this.officer_number = i;
	}

	@Override
	public void run() {
		System.out.println("Loan Officer " + this.officer_number + " created");
		while(true){
			try {
				// Wait for the customer
				BankSimulation.sem_ready_for_officer.acquire();
				
				// Pull customer from line 
				BankSimulation.sem_officer_line.acquire();
				Customer current = BankSimulation.officer_line.poll();
				System.out.println("Loan Officer " + this.officer_number + " begins serving customer " + current.get_num());
				BankSimulation.sem_officer_line.release();
				current.set_current_employee(this.officer_number);
				
				// Signal to customer (officer is ready)
				BankSimulation.sem_employee_assigned[current.get_num()].release();
				
				// Wait for the customer request
				BankSimulation.sem_customer_request_officer.acquire();
				
				processLoan(current);
				
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		}
	}

	private void processLoan(Customer curr) throws InterruptedException {
		int update_balance = curr.get_balance() + curr.get_current_task_amount();
		curr.set_balance(update_balance);

		int update_loan = curr.get_loan_amount() + curr.get_current_task_amount();
		curr.set_loan_amount(update_loan);
		Thread.sleep(400);
		System.out.println("Loan Officer " + this.officer_number + " approves "
				+ "loan of $" + curr.get_current_task_amount() + " for customer " + curr.get_num());
		BankSimulation.sem_perform_task[curr.get_num()].release();
	}

}
