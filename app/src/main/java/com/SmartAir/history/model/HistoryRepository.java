package com.SmartAir.history.model;
import com.google.firebase.firestore.FieldPath;

import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.presenter.FilterDataModel;
import com.SmartAir.history.presenter.HistoryItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.SmartAir.onboarding.model.CurrentUser;


import java.util.ArrayList;
import java.util.List;

/**
 * Repository layer for the History feature. Responsible for communicating with database,
 * applying all filter conditions, and returning the resulting list of HistoryItem
 * objects to the HistoryPresenter.
 */
public class HistoryRepository implements HistoryContract.Repository {

    private final CollectionReference ref;

    public HistoryRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("daily_check_ins");
    }
    @Override
    public void getData(FilterDataModel filter, LoadCallback callback){
        CurrentUser user = CurrentUser.getInstance();
        String parentId = user.getUid();

        Query query = ref.whereEqualTo("parentId", parentId);
        Query filteredQuery = filterQuery(query, filter);
        returnData(filteredQuery, callback);
        System.out.println("HistoryRepository parentId = " + parentId);
    }

    private Query filterQuery(Query query, FilterDataModel filter){

        if (filter.getNightWaking() != null){
            query = query.whereEqualTo("Night Waking", filter.getNightWaking());
        }

        if (filter.getLimitedAbility() != null){
            query =  query.whereEqualTo("Limited Ability", filter.getLimitedAbility());
        }

        if (filter.getSick() != null){
            query = query.whereEqualTo(FieldPath.of("Cough/Wheeze"), filter.getSick());
        }

        if (filter.getStartDate() != null) {
            query = query.whereGreaterThanOrEqualTo("Date", filter.getStartDate());
        }

        if (filter.getEndDate() != null) {
            query = query.whereLessThanOrEqualTo("Date", filter.getEndDate());
        }

        if (filter.getTriggers() != null && !filter.getTriggers().isEmpty()) {
            if (filter.getTriggers().size() == 1) {
                query = query.whereArrayContains("Triggers", filter.getTriggers().get(0));
            } else {
                query = query.whereArrayContainsAny("Triggers", filter.getTriggers());
            }
        }

        return query;
    }

    private void returnData(Query filteredQuery, LoadCallback callback){
        filteredQuery.get().addOnSuccessListener(snapshot->{
            List<HistoryItem> result = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot) {
                String date = doc.getString("Date");
                String childName = doc.getString("Child");
                String author = doc.getString("Entry Author");
                Boolean nightWaking = doc.getBoolean("Night Waking");
                Object sickObj = doc.get(FieldPath.of("Cough/Wheeze"));
                Boolean sick = null;
                if (sickObj instanceof Boolean) {
                    sick = (Boolean) sickObj;
                }
                Boolean limitedAbility = doc.getBoolean("Limited Ability");
                Object triggersObj = doc.get("Triggers");
                List<String> triggers = null;
                if (triggersObj instanceof List) {
                    triggers = (List<String>) triggersObj;
                }
                if (triggers == null) {
                    triggers = new ArrayList<>();
                }

                HistoryItem item = new HistoryItem(date, author, childName, nightWaking,
                        limitedAbility, sick, triggers);
                result.add(item);
            }
            callback.onSuccess(result);
        }).addOnFailureListener(callback::onFailure);
    }
}
