package nnthien.com.myapp.Holder;

import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by T440S on 7/13/2017.
 */

public class QBUsersHolder {
    private static QBUsersHolder instance;
    private SparseArray<QBUser> qbUserSparseArray;
    public static synchronized QBUsersHolder getInstance(){
        if (instance == null){
            instance = new QBUsersHolder();
        }
        return instance;
    }
    private QBUsersHolder(){qbUserSparseArray = new SparseArray<>();}
    public void putUsers(List<QBUser> users){
        for (QBUser user : users){
            putUser(user);
        }
    }

    public void putUser(QBUser user) {
        qbUserSparseArray.put(user.getId(),user);
    }
    public QBUser getUserById(int id){
        return qbUserSparseArray.get(id);
    }
    public List<QBUser> getUsersByIds(List<Integer> Ids){
        List<QBUser> qbUser = new ArrayList<>();
        for (Integer Id : Ids){
            QBUser user = getUserById(Id);
            if(user !=null){
                qbUser.add(user);
            }
        }
        return  qbUser;
    }
    public ArrayList<QBUser> getAllUsers(){
        ArrayList<QBUser> result = new ArrayList<>();
        for (int i = 0; i < qbUserSparseArray.size();i++){
            result.add(qbUserSparseArray.valueAt(i));
        }
        return result;
    }

}
