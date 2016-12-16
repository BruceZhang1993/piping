package indi.shinado.piping.storage;


public interface IDataSnapshot {

    public IDataSnapshot child(String name);

    public <T> T getValue(Class<T> c);

}
