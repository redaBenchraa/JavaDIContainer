
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BonjourDI{
    private HashMap<Class,Class> registry;
    private HashMap<Class, Object> instances;
    private Class tmpClass;
    BonjourDI(){
        registry = new HashMap<Class, Class>();
        instances = new HashMap<Class, Object>();
    }

    public BonjourDI bind(Class baseClass){
        registry.put(baseClass, null);
        tmpClass = baseClass;
        return this;
    }

    public void to(Class implementation){
        registry.remove(tmpClass);
        registry.put(tmpClass, implementation);
    }

    public <T> T newInstance(Class<T> c) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        T object = null;
        if(instances.containsKey(c)){
            object = (T) instances.get(c);
        }else{
            Constructor<T> constructor = getConstructor(c);
            if(constructor != null){
                final Parameter[] parameters = constructor.getParameters();
                final List<Object> arguments = Arrays.stream(parameters)
                        .map(param -> {
                            try {
                                return  newInstance(param.getType());
                            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .collect(Collectors.toList());
                if(arguments.size() > 0){
                    object = constructor.newInstance(arguments.toArray());
                }else{
                    object = constructor.newInstance();
                }
            }

            instances.put(c, object);
        }
        return  object;
    }


    private <T> Constructor getConstructor(Class<T> c){
        if(registry.containsKey(c)){
            final Constructor<?>[] constructors = registry.get(c).getConstructors();
            if(constructors.length > 0){
                return registry.get(c).getConstructors()[0];
            }
        }
        return null;
    }
}
