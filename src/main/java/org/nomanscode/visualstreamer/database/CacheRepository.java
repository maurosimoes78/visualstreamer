package org.nomanscode.visualstreamer.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.nomanscode.visualstreamer.common.*;
import org.nomanscode.visualstreamer.exceptions.CybertronException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CacheRepository<K, V> //extends ObservableRepository<K, V>
{
    /*@Autowired
    private JdbcTemplate jdbcTemplate;*/

    private static ReentrantReadWriteLock theLock = new ReentrantReadWriteLock();
    private Map<K, V> cache = new LinkedHashMap<>();
    private volatile boolean isCacheValid;

    protected Map<K, V> getCache() {
        return this.cache;
    }

    protected void readLock() throws InterruptedException
    {
        ThreadUtils.lock(theLock.readLock());
    }

    protected void readUnlock()
    {
        ThreadUtils.unlock(theLock.readLock());
    }

    protected void writeLock() throws InterruptedException
    {
        ThreadUtils.lock(theLock.writeLock());
    }

    protected void writeUnlock()
    {
        ThreadUtils.unlock(theLock.writeLock());
    }

    protected void loadCache() throws CybertronException, InterruptedException
    {
        if (!isCacheValid) {
            readUnlock();
            writeLock();
            try {
                if (!isCacheValid) {
                    isCacheValid = true;
                    cache.clear();

                    /*try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
                        conn.setAutoCommit(false);
                        try {
                            loadCache(conn, cache);
                            conn.commit();
                        } catch (Exception ex) {
                            conn.rollback();
                            throw ex;
                        }
                    } catch (Exception ex) {
                        isCacheValid = false;
                        throw new CybertronException(ErrorCode.DATABASE_ERROR, "An error occurred while loading cache in " + this.getClass().getName(), ex.getMessage(), ex);
                    }*/
                }
            } finally {
                readLock();
                writeUnlock();
            }
        }
    }

    protected abstract void loadCache(Connection conn, Map<K, V> cache) throws SQLException, InterruptedException, IOException;

    /*protected rx.Observable<Update<K, V>> createObservable()
    {
        try {
            readLock();
            try {
                loadCache();

                return rx.Observable.from(cache.entrySet()
                                               .stream()
                                               .map(e -> new Update<>(Update.Type.Set, e.getKey(), copy(e.getValue())))
                                               .collect(Collectors.toList()));
            } finally {
                readUnlock();
            }
        } catch (Throwable t) {
            return rx.Observable.error(t);
        }
    }*/

    public List<V> get(Predicate<V> filter, Comparator<V> sorter) throws CybertronException, InterruptedException
    {
        readLock();
        try {
            loadCache();

            List<V> list = new ArrayList<>();

            for (V t : cache.values()) {
                if (filter == null || filter.test(t)) {
                    list.add(t);
                }
            }

            if (sorter != null) {
                Collections.sort(list, sorter);
            }
            return list;
        } finally {
            readUnlock();
        }
    }

    public List<V> get(Predicate<V> filter) throws CybertronException, InterruptedException
    {
        return get(filter, null);
    }

    public List<V> get(Comparator<V> sorter) throws CybertronException, InterruptedException
    {
        return get(null, sorter);
    }

    public List<V> get() throws CybertronException, InterruptedException
    {
        return get(null, null);
    }

    public V get(K key) throws CybertronException, InterruptedException
    {
        readLock();
        try {
            loadCache();
            V v = cache.get(key);
            return (v != null) ? v : null;
        } finally {
            readUnlock();
        }
    }

    public V set(V value) throws CybertronException, InterruptedException
    {
        V v;

        writeLock();
        try {
            /*try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
                conn.setAutoCommit(false);
                try {
                    v = set(conn, value);
                    conn.commit();
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new CybertronException(ErrorCode.DATABASE_ERROR, "An error occurred while setting object in " + this.getClass().getName(), ex.getMessage(), ex);
            }*/

            //cache.put(key(v), copy(v));
            //notifySet(v);
        } finally {
            writeUnlock();
        }

        return null;
    }

    protected abstract V set(Connection conn, V value) throws SQLException, InterruptedException, JsonProcessingException;

    public boolean del(K key) throws CybertronException, InterruptedException
    {
        boolean result;

        writeLock();
        try {
            /*try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
                conn.setAutoCommit(false);
                try {
                    result = del(conn, key);
                    conn.commit();
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new CybertronException(ErrorCode.DATABASE_ERROR, "An error occurred while deleting object in " + this.getClass().getName(), ex.getMessage(), ex);
            }*/

            cache.remove(key);
            //notifyDel(key);
        } finally {
            writeUnlock();
        }

        return true;
    }

    protected abstract boolean del(Connection conn, K key) throws SQLException, InterruptedException;

    public int count() throws InterruptedException
    {
        readLock();
        try {
            loadCache();
            return cache.size();
        } finally {
            readUnlock();
        }
    }

    public void setTimestampSafe(PreparedStatement stmt, int fieldNo, Date date) throws SQLException {
        if ( date == null ) {
            stmt.setNull(fieldNo, java.sql.Types.TIMESTAMP);
            return;
        }

        stmt.setTimestamp(fieldNo, new java.sql.Timestamp(date.getTime()));
    }
}
