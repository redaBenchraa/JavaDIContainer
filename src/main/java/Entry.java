public class Entry {
    private Class AbstractClass;
    private Class ImplementationClass;
    Entry(Class a, Class i){
        AbstractClass = a;
        ImplementationClass = i;
    }
    public Entry(Class a){
        AbstractClass = a;
    }
    public void to(Class c) throws BindingException {
        setImplementationClass(c);
    }
    public Class getAbstractClass() {
        return AbstractClass;
    }

    public void setAbstractClass(Class abstractClass) {
        ImplementationClass = null;
    }

    public Class getImplementationClass() {
        return ImplementationClass;
    }

    public void setImplementationClass(Class implementationClass) throws BindingException {
        if(implementationClass != null && getAbstractClass().isAssignableFrom(implementationClass)){
            ImplementationClass = implementationClass;
        }else{
            throw new BindingException("Classes are not related");
        }
    }
}
