package dtu.ws.fastmoney.database;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.Transaction;
import dtu.ws.fastmoney.persistency.Repository;
import dtu.ws.fastmoney.persistency.UnitFunction;

//public class MysqlResource implements Repository {
//	
//	private static Repository instance = new MysqlResource();
//	
//	public static Repository getInstance() {
//		return instance;
//	}
//	
//	private MysqlResource() {}
//
//	EntityManagerFactory emf = Persistence.createEntityManagerFactory("bank");
//	EntityManager em = emf.createEntityManager();
//
//	@Override
//	public void createAccount(Account a) {
//			em.persist(a);
//	}
//
//	@Override
//	public Account readAccount(String id) {
//		try {
//			TypedQuery<Account> mediaQuery = em.createQuery("SELECT a FROM Account a WHERE a.id=:id", Account.class)
//					.setParameter("id", id);
//			return mediaQuery.getSingleResult();
//		} catch (NoResultException e) {
//			return null;
//		}
//	}
//
//	@Override
//	public void deleteAccount(String id) {
//			Account a = readAccount(id);
//			em.createQuery("DELETE FROM Account a WHERE a.id=:id").setParameter("id", id).executeUpdate();
//			for (Transaction t : a.getTransactions()) {
//				this.deleteTransaction(t);
//			}
//	}
//
//	@Override
//	public void updateAccount(Account a) {
//			em.persist(a);
//	}
//
//	@Override
//	public void executeInTransaction(UnitFunction f) throws BankServiceException {
//		startRepositoryTransaction();
//		try {
//			f.unitFunction();
//			commitRepositoryTransaction();
//		} catch (Throwable e) {
//			rollbackRepositoryTransaction();
//			throw e;
//		}
//	}
//	
//	@Override
//	public void createAccountTransaction(Transaction t) {
//		em.persist(t);
//	}
//
//	@Override
//	public Account readAccountByCpr(String cprNumber) {
//		try {
//			TypedQuery<Account> mediaQuery = em
//					.createQuery("SELECT a FROM Account a WHERE a.user.cprNumber=:cpr", Account.class)
//					.setParameter("cpr", cprNumber);
//			return mediaQuery.getSingleResult();
//		} catch (NoResultException e) {
//			return null;
//		}
//	}
//
//	@Override
//	public List<Account> readAccounts() {
//		TypedQuery<Account> mediaQuery = em
//				.createQuery("SELECT a FROM Account a", Account.class);
//		return mediaQuery.getResultList();
//	}
//
//	private void deleteTransaction(Transaction t) {
//		em.createQuery("DELETE FROM Transaction t WHERE t.id=:id").setParameter("id", t.getId()).executeUpdate();
//	}
//
//	private void rollbackRepositoryTransaction() {
//		System.out.println("Rollback Transaction ...");
//		em.getTransaction().rollback();
//		System.out.println("Rollback Transaction ... done");
//	}
//	
//	private void startRepositoryTransaction() {
//		System.out.println("Start Transaction: ...");
//		em.getTransaction().begin();
//		System.out.println("Start Transaction: ... done");
//	}
//
//	private void commitRepositoryTransaction() {
//		System.out.println("Commit Transaction: ...");
//		em.getTransaction().commit();
//		System.out.println("Commit Transaction: ... done");
//	}
//	
//}
