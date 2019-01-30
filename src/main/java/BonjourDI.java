
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BonjourDI{
    private HashMap<Class,List<Class>> registry;
    private List<Class> registryAutowire;
    private List<Class> singletons;
    private HashMap<Class, Object> instances;
    private HashMap<Class, Object> objects;
    private Class tmpClass;
    BonjourDI(){
        registryAutowire = new ArrayList<>();
        registry = new HashMap<>();
        instances = new HashMap<>();
        objects = new HashMap<>();
        singletons = new ArrayList<>();
    }

    public BonjourDI bind(Class baseClass){
        if(!registry.containsKey(baseClass)){
            registry.put(baseClass, null);
        }
        tmpClass = baseClass;
        return this;
    }


    public BonjourDI to(Class implementation){
        InsertInRegistry(implementation);
        return this;
    }

    private void InsertInRegistry(Class implementation) {
        if(registry.get(tmpClass) == null){
            registry.remove(tmpClass);
        }
        if(!registry.containsKey(tmpClass)){
            ArrayList<Class> list = new ArrayList<>();
            list.add(implementation);
            registry.put(tmpClass,list);
        }else {
            registry.get(tmpClass).add(implementation);
        }
        tmpClass = implementation;
    }

    public void forAutowiring(){
        registryAutowire.add(tmpClass);
        InsertInRegistry(tmpClass);
    }

    public void asSingleton(){
        singletons.add(tmpClass);
    }

    public void to(Object obj){
        objects.put(tmpClass, obj);
    }

    public <T> T newInstance(Class<T> c) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return newInstance(c, null);
    }
    public <T> T newInstance(Class<T> c, Class defaultImplementation) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        //If singletion
        T resultedObject = singletons.contains(getImplementation(c, defaultImplementation)) ? (T) instances.get(getImplementation(c, defaultImplementation)) : null;
        if (resultedObject != null) {
            return  resultedObject;
        } else {

            Constructor<T> constructor = getConstructor(getImplementation(c, defaultImplementation));
            if(constructor != null){
                final Parameter[] parameters = constructor.getParameters();
                Class[] defaultImplementations = new Class[0];
                if(constructor.isAnnotationPresent(Inject.class)){
                    defaultImplementations = constructor.getAnnotation(Inject.class).defaultImplementations();
                }
                Class[] finalDefaultImplementations = defaultImplementations;
                final List<Object> arguments = Arrays.stream(parameters)
                        .map(param -> {
                            try {
                                if(objects.containsKey(param.getType())){
                                    return  objects.get(param.getType());
                                }else{
                                    List<Class> imps = Arrays.stream(finalDefaultImplementations)
                                            .filter(x -> param.getType().isAssignableFrom(x))
                                            .collect(Collectors.toList());
                                    return  newInstance(param.getType(), imps.size() > 0 ? imps.get(0) : null);
                                }
                            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .collect(Collectors.toList());
                if(arguments.size() > 0){
                    resultedObject = constructor.newInstance(arguments.toArray());
                }else{
                    resultedObject = constructor.newInstance();
                }
                // Inject in setter
                List<Method> setterMethods = GetSetterMethods(c);
                T tempObject = resultedObject;
                setterMethods.forEach(method -> {
                    try {
                        method.invoke(tempObject, getMethodArgument(method).toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });

                // Inject fields
                getFields(c).forEach(field -> {
                    try {
                        Class imp = field.getAnnotation(Inject.class).defaultImplementation();
                        imp = imp == Class.class ? null : imp;
                        imp = imp != null && field.getType().isAssignableFrom(imp) ? imp : null;
                        field.set(tempObject, newInstance(field.getType(), imp));
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                    }
                });


                //Autowiring
                Arrays.stream(c.getFields()).filter(x -> x.isAnnotationPresent(Inject.class)).forEach(field -> {
                    List<Class> imp = registryAutowire
                            .stream()
                            .filter(x -> x.getName().equals(field.getName()))
                            .collect(Collectors.toList());
                    if(imp.size() > 0) {
                        try {
                            field.set(tempObject, newInstance(imp.get(0)));
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

                resultedObject = tempObject;
            }
            if(singletons.contains(getImplementation(c, defaultImplementation)) && !instances.containsKey(getImplementation(c, defaultImplementation))){
                instances.put(getImplementation(c, defaultImplementation), resultedObject);
            }
            return resultedObject;
        }
    }

    private Class getImplementation(Class c, Class imp){
        if(imp == null){
            return registry.get(c) != null && registry.get(c).size() > 0 ? registry.get(c).get(0) : c;
        }else{
            List<Class> classes = registry.get(c).stream().filter(x -> x == imp).collect(Collectors.toList());
            if(classes.size() > 0)
                return classes.get(0);
            return Class.class;
        }
    }

    public List<?> getMethodArgument(Method method){
        return Arrays.stream(method.getParameterTypes()).map(
                type -> {
                    try {
                        return newInstance(type);
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());
    }


    private <T> Constructor getConstructor(Class c){
        final List InjectedConstructors;
        InjectedConstructors = Arrays.stream(c.getConstructors()).filter(x -> x.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        if(InjectedConstructors.size() > 1)
            return null;
        else if(InjectedConstructors.size() == 1)
            return (Constructor) InjectedConstructors.get(0);
        else if(c.getConstructors().length > 0){
            List<Constructor> constructorsWithNoParams = Arrays.stream(c.getConstructors()).filter(x -> x.getParameterTypes().length == 0).collect(Collectors.toList());
            if(constructorsWithNoParams.size() > 0) {
                return constructorsWithNoParams.get(0);
            }else{
                return null;
            }
        }
        return null;
    }

    public static boolean isSetter(Method method){
        if(!method.getName().startsWith("set")) return false;
        return method.getParameterTypes().length == 1;
    }

    public boolean CanInjectParamInMethod(Method method){
        return Arrays.stream(method.getParameterTypes()).filter(x -> registry.containsKey(x)).count() > 0;
    }

    public List<Method> GetSetterMethods(Class c){
        return Arrays.stream(c.getDeclaredMethods())
                .filter(x -> isSetter(x) && CanInjectParamInMethod(x))
                .collect(Collectors.toList());
    }

    public List<Field> getFields(Class c){
        return Arrays.stream(c.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
    }
}
