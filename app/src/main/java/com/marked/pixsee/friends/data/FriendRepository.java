package com.marked.pixsee.friends.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.mapper.CursorToUserMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.UserToCvMapper;
import com.marked.pixsee.friends.data.datasource.FriendsDatasource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Tudor Pop on 12-Dec-15.
 * Singleton class used to keep all the friends of the user
 */
public class FriendRepository implements FriendsDatasource {
    private FriendsDatasource disk;
    private FriendsDatasource network;
    final List<User> cache = new ArrayList<>(10);
    private boolean dirtyCache;

    public FriendRepository(@NotNull FriendsDatasource disk, @NotNull FriendsDatasource network) {
        this.disk = disk;
        this.network = network;
    }

    private Mapper<Cursor, User> cursorToUserMapper = new CursorToUserMapper();
    private Mapper<User, ContentValues> userToCvMapper = new UserToCvMapper();

    public int length() {
        return cache.size();
    }

    @Override
    public Observable<List<User>> getUsers() {
        if (cache.size() != 0 && !dirtyCache)
            return Observable.just(cache);

        // Query the local storage if available. If not, query the network.
        Observable<List<User>> local = disk.getUsers()
                .doOnNext(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        cache.clear();
                        cache.addAll(users);
                    }
                });
        Observable<List<User>> remote = network.getUsers()
                .flatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        disk.saveUser(users);
                        return Observable.from(users);
                    }
                })
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        cache.clear();
                        cache.add(user);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        dirtyCache = false;
                    }
                }).toList();
    /* http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/ */
        return Observable.concat(Observable.just(cache), local, remote)
                .first(new Func1<List<User>, Boolean>() {
                    @Override
                    public Boolean call(List<User> users) {
                        return users.size() > 0;
                    }
                })
                .flatMap(new Func1<List<User>, Observable<List<User>>>() {
                    @Override
                    public Observable<List<User>> call(List<User> users) {return Observable.from(users).toSortedList();
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void saveUser(@NonNull List<User> users) {
        disk.saveUser(users);
        network.saveUser(users);
        cache.addAll(users);
    }

    @Override
    public Observable<List<User>> refreshUsers() {
        dirtyCache = true;
        cache.clear();
        disk.deleteAllUsers();
        return getUsers();
    }

    @Override
    public Observable<User> getUser(@NonNull User userId) {
        return Observable.empty();
    }

    @Override
    public void saveUser(@NonNull User item) {
        network.saveUser(item);
        disk.saveUser(item);
        cache.set(cache.indexOf(item), item);
    }

    @Override
    public void deleteAllUsers() {

    }

    @Override
    public void deleteUsers(@NonNull User userId) {
        disk.deleteUsers(userId);
        network.deleteUsers(userId);
    }
}
