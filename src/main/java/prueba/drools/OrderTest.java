package prueba.drools;

import java.util.Arrays;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import prueba.drools.drools.Customer;
import prueba.drools.drools.Order;
import prueba.drools.drools.Product;

 
public class OrderTest {
 
	public static final void main(String[] args) {
		final KnowledgeBase kbase = readKnowledgeBase();
		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
 
		final List<Order> orders = Arrays.asList(getOrderWithDefaultCustomer(), getOrderWithSilverCustomer(),
				getOrderWithGoldCustomer(), getOrderWithGoldCustomerAndTenProducts());
		for (Order order : orders) {
			ksession.insert(order);
		}
		ksession.fireAllRules();
 
		showResults(orders);
	}
 
	private static KnowledgeBase readKnowledgeBase() {
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		 //Resource ruleFile = ResourceFactory.newFileResource("src/main/resources/defaultkiesession/Hal1.drl");
		 
		kbuilder.add(ResourceFactory.newClassPathResource("Order.drl"), ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Imposible crear knowledge con Order.drl");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
 
	private static Order getOrderWithDefaultCustomer() {
		final Order order = new Order(getDefaultCustomer());
		order.addProduct(getProduct1());
		return order;
	}
 
	private static Order getOrderWithSilverCustomer() {
		final Order order = new Order(getSilverCustomer());
		order.addProduct(getProduct1());
		return order;
	}
 
	private static Order getOrderWithGoldCustomer() {
		final Order order = new Order(getGoldCustomer());
		order.addProduct(getProduct1());
		return order;
	}
 
	private static Order getOrderWithGoldCustomerAndTenProducts() {
		final Order order = new Order(getSilverCustomer());
		for (int i = 0; i < 10; i++) {
			order.addProduct(getProduct1());
		}
		return order;
	}
 

	// Datos de prueba. Clientes y producto
	private static Customer getDefaultCustomer() {
		return new Customer(Customer.DEFAULT_CUSTOMER, "Cliente estandar");
	}
 
	private static Customer getSilverCustomer() {
		return new Customer(Customer.SILVER_CUSTOMER, "Cliente SILVER");
	}
 
	private static Customer getGoldCustomer() {
		return new Customer(Customer.GOLD_CUSTOMER, "Cliente GOLD");
	}
 
	private static Product getProduct1() {
		return new Product(1, "Producto 1", 100d);
	}
 
	private static void showResults(List<Order> orders) {
		for (Order order : orders) {
			System.out.println("Cliente " + order.getCustomer().getName() + " productos: " + order.getProducts().size()
					+ " Precio total: " + order.getTotalPrice());
		}
	}
}
