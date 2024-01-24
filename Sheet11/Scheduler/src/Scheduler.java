import java.util.*;

/**
 *
 * To execute the program you can use:
 *
 * java Scheduler.java
 *
 * You have to implement the immediateRestart and wait_die methods,
 * which should return a 2PL schedule if using the given deadlock prevention
 * strategy.
 * 
 * You can of course also make changes to all other given classes if required.
 * 
 * This template specifies a class representing histories (and schedules) and
 * multiple classes representing operations of histories.
 * 
 * Use the Wait class to indicate a waiting transaction.
 */
public class Scheduler {

  public static void main(String[] args) {
    checkS1();
  }

  private static void checkS1() {
    History s1 = new History();
    s1.addOperation(new Write(1, "x"));
    s1.addOperation(new Read(2, "x"));
    s1.addOperation(new Write(3, "y"));
    s1.addOperation(new Read(1, "y"));
    s1.addOperation(new Read(3, "z"));
    s1.addOperation(new Write(1, "x"));
    s1.addOperation(new Commit(1));
    s1.addOperation(new Write(2, "y"));
    s1.addOperation(new Commit(2));
    s1.addOperation(new Write(3, "y"));
    s1.addOperation(new Commit(3));

    System.out.println("==== S1 ====");
    System.out.println("s1 = " + s1.toString());
    System.out.println("Immediate restart = " + immediateRestart(s1).toString());
    System.out.println("Wait-die = " + wait_die(s1).toString());
  }

  public static History immediateRestart(History s) {
    //
    // TODO Your code here!
    //
    return new History();
  }

  public static History wait_die(History s) {
    //
    // TODO Your code here!
    //
    return new History();
  }


}

/**
 * Represents a history (and schedule, as schedules are prefixes of histories)
 * as introduced in the lecture.
 */
class History {
  private ArrayList<Operation> operations;

  /**
   * Creates a new empty history
   */
  public History() {
    operations = new ArrayList<>();
  }

  /**
   *
   * @return the list of all operations in the history
   */
  public List<Operation> getOperations() {
    return operations;
  }

  /**
   * Adds a new operation to the history
   * 
   * @param op the operation to add
   */
  public void addOperation(Operation op) {
    this.operations.add(op);
  }

  /**
   * Creates a new history where all aborted transactions are removed.
   * 
   * @return New history not containing any aborted transactions
   */
  public History removeAbortedTransactions() {
    History h = new History();
    for (int i = 0; i < this.operations.size(); i++) {
      int transaction = this.operations.get(i).getTransaction();
      if (this.operations.subList(i + 1, this.operations.size()).stream()
          .filter(x -> x.getTransaction() == transaction && x.getClass().isInstance(Abort.class)).count() == 0) {
        h.addOperation(this.operations.get(i));
      }
    }
    return h;
  }

  @Override
  public String toString() {
    return operations.toString();
  }

}

/**
 * Represents a single operation of a history. The operation types are
 * structured as follows: -------------Operation-----------------------------
 * Wait | | --ScheduleOperation-- --SynchronizationOperation---- | | | |
 * IOOperation Commit/Abort LockOperation UnlockOperation | | | Read/Write
 * ReadLock/WriteLock ReadUnlock/WriteUnlock
 */
abstract class Operation {
  protected int transaction;
  protected String page;

  /**
   * Initializes a new operation of a given transaction and page
   * 
   * @param transaction The transaction number
   * @param page        The page this operation is executed on ("" if not
   *                    applicable)
   */
  public Operation(int transaction, String page) {
    this.transaction = transaction;
    this.page = page;
  }

  /**
   * 
   * @return The transaction ID
   */
  public int getTransaction() {
    return transaction;
  }

  /**
   * 
   * @return The page of this operation
   */
  public String getPage() {
    return page;
  }
}

/**
 * Represents all operations that can be contained in a schedule (e.g.: read,
 * write, commit, abort)
 */
abstract class ScheduleOperation extends Operation {
  public ScheduleOperation(int transaction, String page) {
    super(transaction, page);
  }
}

/**
 * Represents the read and write operations
 */
abstract class IOOperation extends ScheduleOperation {
  public IOOperation(int transaction, String page) {
    super(transaction, page);
  }

  /**
   * Returns the LockOperation required for this IOOperation. Read operations
   * return ReadLock and write operations return WriteLock
   */
  public abstract LockOperation getLockOperation();
}

/**
 * Represents all operations used for synchronization (e.g.: lock and unlock
 * operations)
 */
abstract class SynchronizationOperation extends Operation {
  public SynchronizationOperation(int transaction, String page) {
    super(transaction, page);
  }
}

/**
 * Represents all lock operations (e.g.: read and write locks)
 */
abstract class LockOperation extends SynchronizationOperation {
  public LockOperation(int transaction, String page) {
    super(transaction, page);
  }

  /**
   * Checks if the given lock is compatible with the other lock, according to the
   * compatibility matrix given in the lecture.
   * 
   * @param other The other LockOperation to check against
   * @return True if compatible, False if not
   */
  public boolean isCompatible(LockOperation other) {
    if (page != other.page)
      return true;
    return false;
  }

  /**
   * Returns the UnlockOperation required to release this lock
   * 
   * @return Fitting UnlockOperation
   */
  abstract public UnlockOperation getUnlockOperation();
}

/**
 * Represents all unlock operations (e.g.: read and write locks)
 */
abstract class UnlockOperation extends SynchronizationOperation {
  public UnlockOperation(int transaction, String page) {
    super(transaction, page);
  }
}

/**
 * The read operation, representing a read of a page in a history.
 */
class Read extends IOOperation {

  public Read(int transaction, String page) {
    super(transaction, page);
  }

  @Override
  public String toString() {
    return "r_" + transaction + "(" + page + ")";
  }

  @Override
  public LockOperation getLockOperation() {
    return new ReadLock(transaction, page);
  }
}

/**
 * The write operation, representing a write of a page in a history.
 */
class Write extends IOOperation {

  public Write(int transaction, String page) {
    super(transaction, page);
  }

  @Override
  public String toString() {
    return "w_" + transaction + "(" + page + ")";
  }

  @Override
  public LockOperation getLockOperation() {
    return new WriteLock(transaction, page);
  }
}

/**
 * Represents a waiting transaction
 */
class Wait extends Operation {

  public Wait(int transaction) {
    super(transaction, "");
  }

  @Override
  public String toString() {
    return "wait_" + transaction;
  }
}

/**
 * Represents a commit operation of a transaction
 */
class Commit extends ScheduleOperation {

  public Commit(int transaction) {
    super(transaction, "");
  }

  @Override
  public String toString() {
    return "c_" + transaction;
  }
}

/**
 * Represents an abort operation of a transaction
 */
class Abort extends ScheduleOperation {

  public Abort(int transaction) {
    super(transaction, "");
  }

  @Override
  public String toString() {
    return "a_" + transaction;
  }
}

/**
 * The releasing unlock operation for a fitting ReadLock operation.
 */
class ReadUnlock extends UnlockOperation {

  public ReadUnlock(int transaction, String page) {
    super(transaction, page);
  }

  @Override
  public String toString() {
    return "ru_" + transaction + "(" + page + ")";
  }

}

/**
 * The releasing unlock operation for a fitting WriteLock operation.
 */
class WriteUnlock extends UnlockOperation {

  public WriteUnlock(int transaction, String page) {
    super(transaction, page);
  }

  @Override
  public String toString() {
    return "wu_" + transaction + "(" + page + ")";
  }
}

/**
 * Represents acquiring a read lock.
 */
class ReadLock extends LockOperation {

  public ReadLock(int transaction, String page) {
    super(transaction, page);
  }

  @Override
  public String toString() {
    return "rl_" + transaction + "(" + page + ")";
  }

  @Override
  public boolean isCompatible(LockOperation other) {
    if (super.isCompatible(other))
      return true;
    return other.getClass().isInstance(ReadLock.class);
  }

  @Override
  public UnlockOperation getUnlockOperation() {
    return new ReadUnlock(transaction, page);
  }

}

/**
 * Represents acquiring a read lock.
 */
class WriteLock extends LockOperation {

  public WriteLock(int transaction, String page) {
    super(transaction, page);
  }

  @Override
  public String toString() {
    return "wl_" + transaction + "(" + page + ")";
  }

  @Override
  public boolean isCompatible(LockOperation other) {
    if (super.isCompatible(other))
      return true;
    return other.page == page && other.transaction == transaction;
  }

  @Override
  public UnlockOperation getUnlockOperation() {
    return new WriteUnlock(transaction, page);
  }
}
