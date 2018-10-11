package com.tinkoff.androidcourse;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Worker> workerList;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workerList = getWorkers();
        recycler = findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new MyAdapter(workerList));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                workerList.remove(position);
                recycler.getAdapter().notifyItemRemoved(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recycler);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Реализовать добавление тестовых работников
                 */

                Worker worker = WorkerGenerator.generateWorker();
                workerList.add(worker);
                if (recycler != null) {
                    recycler.getAdapter().notifyItemRangeChanged(
                            workerList.size() - 1,
                            1
                    );
                }
            }
        });

        /**
         * Реализовать адаптер, выбрать любой LayoutManager и прикрутить это всё к RecyclerView
         *
         * Тестовые данные для отображения генерятся WorkerGenerator
         */
    }

    private List<Worker> getWorkers() {
        Object workers = getLastCustomNonConfigurationInstance();
        if (workers instanceof List) {
            return (List<Worker>) workers;
        }

        return WorkerGenerator.generateWorkers(3);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return workerList;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView ageTextView;
        private final TextView positionTextView;
        private final AppCompatImageView photoImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            ageTextView = itemView.findViewById(R.id.age_text_view);
            positionTextView = itemView.findViewById(R.id.position_text_view);
            photoImageView = itemView.findViewById(R.id.photo_image_view);
        }

        public void bindWorker(Worker worker) {
            nameTextView.setText(worker.getName());
            ageTextView.setText(worker.getAge());
            positionTextView.setText(worker.getPosition());
            photoImageView.setImageResource(worker.getPhoto());
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final List<Worker> workerList;

        public MyAdapter(List<Worker> workerList) {
            this.workerList = workerList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_layout, viewGroup, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            myViewHolder.bindWorker(workerList.get(i));
        }

        @Override
        public int getItemCount() {
            return workerList.size();
        }
    }
}
