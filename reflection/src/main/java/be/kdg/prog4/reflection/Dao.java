package be.kdg.prog4.reflection;

public interface Dao {
    public void createTable();

    public void create(Object object);

    public Object retrieve(String key);

    public void update(Object object);

    public void delete(Object object);
}
