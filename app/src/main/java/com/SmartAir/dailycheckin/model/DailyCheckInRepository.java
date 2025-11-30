package com.SmartAir.dailycheckin.model;
import com.SmartAir.dailycheckin.presenter.Date;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.presenter.DailyCheckInDataModel;
import java.util.Set;

public class DailyCheckInRepository implements DailyCheckInContract.Repository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DailyCheckInRepository(){}
    @Override
    public void sendDataToDatabase(DailyCheckInDataModel data,
                                   DailyCheckInContract.Repository.SaveCallback callback){

        Map<String, Object> map = new HashMap<>();
        map.put("Entry Author", data.getEntryAuthor());
        map.put("Date", data.getDate());
        map.put("Child", data.getChildName());
        map.put("parentId", data.getParentId());
        map.put("Night Waking", data.getNightWaking());
        map.put("Limited Ability", data.getLimitedAbility());
        map.put("Cough/Wheeze", data.getSick());
        map.put("Triggers", data.getTriggers());

        db.collection("daily_check_ins").add(map).addOnSuccessListener(
                doc -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    @Override
    public void loadChildren(String parentId, LoadChildrenCallback callback) {
        db.collection("Users")
                .document(parentId)
                .get()
                .addOnSuccessListener(parentDoc -> {
                    List<String> childIds = (List<String>) parentDoc.get("childrenIds");

                    if (childIds == null || childIds.isEmpty()) {
                        callback.onChildrenLoaded(new ArrayList<>());
                        return;
                    }

                    loadChildNames(childIds, callback);
                })
                .addOnFailureListener(callback::onError);
    }

    private void loadChildNames(List<String> childIds, LoadChildrenCallback callback) {
        List<String> resultNames = new ArrayList<>();

        for (String id : childIds) {
            db.collection("Users")
                    .document(id)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String name = doc.getString("displayName");
                        resultNames.add(name != null ? name : "Unknown");

                        if (resultNames.size() == childIds.size()) {
                            filterNames(resultNames, callback);
                        }
                    })
                    .addOnFailureListener(callback::onError);
        }
    }

    private void filterNames(List<String> childNames, LoadChildrenCallback callback) {
        String today = Date.getCurrentDate();

        db.collection("daily_check_ins")
                .whereEqualTo("Date", today)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    Set<String> checkedNames = new HashSet<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("Child");
                        if (name != null) {
                            checkedNames.add(name);
                        }
                    }

                    List<String> remaining = new ArrayList<>();
                    for (String name : childNames) {
                        if (!checkedNames.contains(name)) {
                            remaining.add(name);
                        }
                    }

                    callback.onChildrenLoaded(remaining);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void checkIfCanSubmit(String childName,
                                 DailyCheckInContract.Repository.SubmitValidityCallback callback){

        String today = Date.getCurrentDate();

        db.collection("daily_check_ins")
                .whereEqualTo("Date", today)
                .whereEqualTo("Child", childName)
                .get()
                .addOnSuccessListener(snapshot -> {
                    boolean entriesExist = !snapshot.isEmpty();
                    callback.onValidity(entriesExist);
                })
                .addOnFailureListener(callback::onNotValid);
    }
}
