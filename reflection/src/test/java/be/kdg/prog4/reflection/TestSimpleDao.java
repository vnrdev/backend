package be.kdg.prog4.reflection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestSimpleDao {

    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.makeConnection();
        DatabaseConnection.connection.setAutoCommit(true);
    }

    @Test
    public void testCreateAndRetrieve() {
        Dao personDao = DaoFactory.createDao(Person.class);
        personDao.createTable(); //Zorg ervoor dat een tabel PERSON met leeftijd, etc. wel degelijk gecreëerd is

        Person person = new Person("Kris", "Arduino", 43);
        personDao.create(person); //INSERT
        Person result = (Person) personDao.retrieve("Kris");
        assertEquals(person, result);
        person = new Person("Bla", "hihi", 13);
        personDao.create(person);
        result = (Person) personDao.retrieve("Bla");
        assertEquals(person, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutAnnotations() {
        DaoFactory.createDao(PersonWithoutAnnotations.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutId() {
        DaoFactory.createDao(PersonWithoutId.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithMultipleIds() {
        DaoFactory.createDao(PersonWithMultipleIds.class);
    }

    @Test
    public void testRetrieveNonExistent() {
        Dao personDao = DaoFactory.createDao(Person.class);
        personDao.createTable();
        Person person = (Person) personDao.retrieve("testje");
        assertNull(person);
    }

    @Test
    public void testUpdate() {
        Dao personDao = DaoFactory.createDao(Person.class);
        personDao.createTable();
        Person person = new Person("Ikke", "bla", 23);
        personDao.create(person);
        person = new Person("Ikke", "boe", 42);
        personDao.update(person);
        Person result = (Person) personDao.retrieve("Ikke");
        assertEquals(person, result);
    }

    @Test
    public void testUpdateWithWrongKey() {
        Dao personDao = DaoFactory.createDao(Person.class);
        personDao.createTable();
        Person person = new Person("Ikke", "bla", 23);
        personDao.create(person);
        person = new Person("Bla", "boe", 42);
        personDao.update(person);
        person = new Person("Ikke", "bla", 23);
        Person result = (Person) personDao.retrieve("Ikke");
        assertEquals(person, result);
        result = (Person) personDao.retrieve("Bla");
        assertNull(result);
    }


    @Test
    public void testDelete() {
        Dao personDao = DaoFactory.createDao(Person.class);
        personDao.createTable();
        Person person = new Person("Ikke", "bla", 23);
        personDao.create(person);
        personDao.delete(person);
        Person result = (Person) personDao.retrieve("Ikke");
        assertNull(result);
    }

    @Test
    public void testDeleteNonExistent() {
        Dao personDao = DaoFactory.createDao(Person.class);
        personDao.createTable();
        Person person = new Person("Ikke", "bla", 23);
        personDao.create(person);
        person = new Person("Bla", "Boe", 12);
        personDao.delete(person);
        person = new Person("Ikke", "bla", 23);
        Person result = (Person) personDao.retrieve("Ikke");
        assertEquals(person, result);
    }

    @After
    public void cleanUp() throws SQLException {
        DatabaseConnection.connection.commit();
        DatabaseConnection.closeConnection();
    }
}
