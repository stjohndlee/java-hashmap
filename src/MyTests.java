import static org.junit.Assert.*;
import org.junit.rules.*;
import org.junit.Test;
import org.junit.Rule;

public class MyTests {
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testInitializingWithIllegalArgument() {
		exception.expect(IllegalArgumentException.class);
		new MyHashMap<>(0);
	}
	
	@Test
	public void testSetAndGet() {
		MyHashMap<String> map = new MyHashMap<>(4);
		map.set("k1", "v1");
		assertEquals("v1",  map.get("k1"));
	}
	
	@Test
	public void testSetDoesntReturnFalse() {
		MyHashMap<String> map = new MyHashMap<>(4);
		assertTrue(map.set("k1", "v1"));
	}
	
	@Test
	public void testSetWithEmptyKey() {
		MyHashMap<String> map = new MyHashMap<>(4);
		map.set("", "v1");
		assertEquals("v1", map.get(""));
	}
	
	@Test
	public void testGetWithNonExistentKey() {
		MyHashMap<String> map = new MyHashMap<>(4);
		map.set("k1", "v1");
		assertNull(map.get("k2"));
	}
	
	@Test
	public void testDelete() {
		MyHashMap<String> map = new MyHashMap<>(4);
		map.set("k1", "v1");
		map.delete("k1");
		assertNull(map.get("k1"));
	}
	
	@Test
	public void testWithCustomObjectAsValue() {
		MyHashMap<MyObject> map = new MyHashMap<>(4);
		MyObject o1 = new MyObject(1);
		MyObject o2 = new MyObject(2);
		MyObject o3 = new MyObject(3);
		MyObject o4 = new MyObject(4);
		
		map.set("k1", o1);
		map.set("k2", o2);
		map.set("k3", o3);
		map.set("k4", o4);
		assertSame(o1, map.get("k1"));
	}
	
	@Test
	public void testOverridingWithSameKey() {
		MyHashMap<MyObject> map = new MyHashMap<>(4);
		MyObject o1 = new MyObject();
		MyObject o2 = new MyObject();
		MyObject o3 = new MyObject();
		MyObject o4 = new MyObject();
		MyObject o5 = new MyObject();
		
		map.set("k1", o1);
		map.set("k1", o2);
		map.set("k1", o3);
		map.set("k1", o4);
		map.set("k1", o5);
		assertSame(o5, map.get("k1"));
	}
	
	@Test
	public void testDeleteReturnsSameObject() {
		MyHashMap<MyObject> map = new MyHashMap<>(4);
		MyObject o1 = new MyObject();
		map.set("k1", o1);
		assertSame(o1, map.delete("k1"));
	}
	
	@Test
	public void testDeleteWithNonExistentKey() {
		MyHashMap<MyObject> map = new MyHashMap<>(4);
		MyObject o1 = new MyObject();
		map.set("k1", o1);
		assertNull(map.delete("k2"));
	}
	
	@Test
	public void testGoingOverCapacity() {
		MyHashMap<MyObject> map = new MyHashMap<>(4);
		map.set("k1", new MyObject());
		map.set("k2", new MyObject());
		map.set("k2", new MyObject()); // overriding
		map.set("k3", new MyObject());
		map.delete("k3"); // deleting
		map.set("k3", new MyObject());
		map.set("k4", new MyObject());
		assertFalse(map.set("k5", new MyObject()));
	}
	
	@Test
	public void testLoad() {
		MyHashMap<String> map = new MyHashMap<>(10);
		map.set("k1", "v1");
		map.set("k2", "v2");
		map.set("k3", "v3");
		map.set("k4", "v4");
		map.set("k5", "v5");
		map.set("k6", "v6");
		map.set("k7", "v7");
		assertEquals(map.load(), 0.7, 0.00001); // 0.00001 is delta
	}
	
	@Test
	public void testLoadWithDeletion() {
		MyHashMap<String> map = new MyHashMap<>(10);
		map.set("k1", "v1");
		map.set("k2", "v2");
		map.delete("k2");
		map.set("k3", "v3");
		map.set("k4", "v4");
		map.set("k5", "v5");
		map.delete("k5");
		map.set("k6", "v6");
		map.set("k7", "v7");
		map.delete("k7");
		assertEquals(map.load(), 0.4, 0.00001); // 0.00001 is delta
	}
	
	@Test
	public void testNestedMyHashMap() {
		MyHashMap<MyHashMap<String>> nestedMap = new MyHashMap<>(4);
		nestedMap.set("k1", new MyHashMap<String>(4));
		nestedMap.set("k2", new MyHashMap<String>(4));
		
		nestedMap.get("k1").set("innerK1", "innerV1");
		nestedMap.get("k1").set("innerK2", "innerV2");
		
		nestedMap.get("k2").set("innerK3", "innerV3");
		
		String[] expecteds = new String[] {"innerV1", "innerV2", "innerV3"};
		String[] actuals = new String[] 
				{nestedMap.get("k1").get("innerK1"), 
				 nestedMap.get("k1").get("innerK2"),
				 nestedMap.get("k2").get("innerK3")};
		
		assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void testSetAndGetWithCollision() {
		MyHashMap<String> map = new MyHashMap<>(4);
		map.set("AaAa", "v1");
		map.set("BBBB", "v2");
		map.set("AaBB", "v3");
		map.set("BBAa", "v4");
		
		String[] expecteds = new String[] {"v1", "v2", "v3", "v4"};
		String[] actuals = new String[] {
				map.get("AaAa"), map.get("BBBB"), 
				map.get("AaBB"), map.get("BBAa")};
		
		assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void testDeleteWithCollision() {
		MyHashMap<String> map = new MyHashMap<>(16);
		map.set("AaAaAa", "v1");
		map.set("AaAaBB", "v2");
		map.set("AaBBAa", "v3");
		map.set("AaBBBB", "v4");
		map.set("BBAaBB", "v5");
		map.set("BBAaAa", "v6");
		map.set("BBBBAa", "v7");
		map.set("BBBBBB", "v8");
		
		map.delete("AaAaAa"); // deleting v1 
		map.delete("AaBBAa"); // deleting v3
		map.delete("AaBBBB"); // deleting v4
		map.delete("BBAaBB"); // deleting v5
		map.delete("BBBBAa"); // deleting v7
		map.delete("BBBBBB"); // deleting v8
		
		String[] expecteds = new String[] {"v2","v6"};
		String[] actuals = new String[] {map.get("AaAaBB"), map.get("BBAaAa")};
		
		assertArrayEquals(expecteds, actuals);
	}
}
