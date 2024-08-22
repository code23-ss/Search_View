package com.example.mainscreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantsActivity";
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private List<String> documentIdList = new ArrayList<>(); // 문서 ID 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // RecyclerView 찾기
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Firestore에서 데이터 비동기적으로 읽기
        loadRestaurantsFromFirestore();

        // SearchView 설정
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRestaurants(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRestaurants(newText);
                return false;
            }
        });
    }

    private void loadRestaurantsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String locationPath = "categories/Location/Subcategories/Seoul";

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        restaurantList.clear();
                        documentIdList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");
                            boolean matchesLocation = false;

                            for (DocumentReference ref : categoryIds) {
                                if (ref.getPath().startsWith(locationPath)) {
                                    matchesLocation = true;
                                    break;
                                }
                            }

                            if (matchesLocation) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurantList.add(restaurant);
                                documentIdList.add(document.getId());
                            }
                        }

                        adapter = new RestaurantAdapter(RestaurantsActivity.this, restaurantList, documentIdList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(RestaurantsActivity.this, "Failed to load restaurant data from Firestore.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void filterRestaurants(String query) {
        List<Restaurant> filteredList = new ArrayList<>();
        List<String> filteredDocIdList = new ArrayList<>();

        for (int i = 0; i < restaurantList.size(); i++) {
            Restaurant restaurant = restaurantList.get(i);
            if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurant);
                filteredDocIdList.add(documentIdList.get(i));
            }
        }

        // 필터된 결과로 어댑터를 업데이트
        adapter = new RestaurantAdapter(RestaurantsActivity.this, filteredList, filteredDocIdList);
        recyclerView.setAdapter(adapter);
    }
}

