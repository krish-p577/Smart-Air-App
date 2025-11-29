package com.SmartAir.dailycheckin.model;

import java.util.Map;
import java.util.HashMap;
import com.google.firebase.firestore.FirebaseFirestore;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.presenter.DailyCheckInDataModel;

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
}
