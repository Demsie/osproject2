
public class Teller implements Runnable {
	
	private int teller_number;
	
	public Teller(int i) {
		this.teller_number = i;
	}

	@Override
	public void run() {
		System.out.println("Teller " + this.teller_number + " created");
		while(true){
			try {
				// Wait for the Customer
				BankSimulation.sem_ready_for_teller.acquire();
				
				// Pull customer from line 
				BankSimulation.sem_teller_line.acquire();
				Customer current = BankSimulation.teller_line.poll();
				System.out.println("Teller " + teller_number + " begins serving customer " + current.get_num());
				BankSimulation.sem_teller_line.release();
				current.set_current_employee(this.teller_number);
				
				// Signal to customer (teller is ready)
				BankSimulation.sem_employee_assigned[current.get_num()].release();
				
				// Wait for the Customer request
				BankSimulation.sem_customer_request_teller[this.teller_number].acquire();
				
				// perform the request(depends on customer's current_task field)
				if(current.get_current_task() == 0){
					process_deposit(current);
				}
				else{
					process_withdrawal(current);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void process_deposit(Customer curr) throws InterruptedException {
		int updated_balance = curr.get_balance() + curr.get_current_task_amount();
		curr.set_balance(updated_balance);
		Thread.sleep(400);
		System.out.println("Teller " + this.teller_number + " processes "
				+ "deposit of $" + curr.get_current_task_amount() + " for customer " + curr.get_num());
		BankSimulation.sem_perform_task[curr.get_num()].release();
	}

	private void process_withdrawal(Customer curr) throws InterruptedException {
		int updated_balance = curr.get_balance() - curr.get_current_task_amount();
		curr.set_balance(updated_balance);
		Thread.sleep(400);
		System.out.println("Teller " + this.teller_number + " processes "
				+ "withdrawal of $" + curr.get_current_task_amount() + " for customer " + curr.get_num());
		BankSimulation.sem_perform_task[curr.get_num()].release();
	}
}
