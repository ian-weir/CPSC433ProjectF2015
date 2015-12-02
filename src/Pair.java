import java.util.Objects;



public class Pair<K,V>{
	private K key;
	private V value;
	
	Pair(K key, V value){
		this.key = key;
		this.value = value;
	}
	
	public K getKey(){
		return key;
	}
	
	public V getValue(){
		return value;
	}
	
	@Override
	public boolean equals(Object o){
		if( !(o instanceof Pair)){
			return false;
		}
		else{
			return Objects.equals(((Pair)o).getKey(), this.key) && Objects.equals(((Pair)o).getValue(), this.value);
		}
	}
	
	//^ is bitwise XOR
	@Override
	public int hashCode(){
		int code;
		int keyCode;
		int valueCode;
		
		if(key == null){
			keyCode = 0;
		}
		else{
			keyCode = key.hashCode();
		}
		
		if(value == null){
			valueCode = 0;
		}
		else{
			valueCode = value.hashCode();
		}
		
		code = keyCode ^ valueCode;
		
		return code;
	}
	
}
