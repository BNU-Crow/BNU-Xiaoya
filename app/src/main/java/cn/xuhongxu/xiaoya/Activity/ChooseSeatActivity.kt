package cn.xuhongxu.xiaoya.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import cn.xuhongxu.LibrarySeat.*
import cn.xuhongxu.xiaoya.Adapter.SeatLayoutItemRecycleAdapter
import cn.xuhongxu.xiaoya.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import cn.xuhongxu.xiaoya.View.ScrollView2D


class ChooseSeatActivity : AppCompatActivity() {

    var layoutInfo: SeatLayout? = null
    val seatLayout = ArrayList<SeatLayoutItem>()

    val buildings = ArrayList<Building>()
    val rooms = ArrayList<Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_seat)

        val preferences = getSharedPreferences(getString(R.string.library_key),
                Context.MODE_PRIVATE)

        val username = preferences.getString("username", "")
        val password = preferences.getString("password", "")

        if (username!!.isEmpty() && password!!.isEmpty()) {
            return
        }

        val client = SeatClient(username, password)
        client.token = intent.getStringExtra("Client")


        val adapter = SeatLayoutItemRecycleAdapter(this, seatLayout, object : SeatLayoutItemRecycleAdapter.OnClickListener {
            override fun OnClicked(item: SeatLayoutItem) {
                val initBuilder = AlertDialog.Builder(this@ChooseSeatActivity)

                initBuilder.setTitle(item.name)
                initBuilder.setMessage("状态：" + item.status + "\n电源：" + item.power + "\n")
                initBuilder.setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                initBuilder.setPositiveButton(R.string.choose_seat) { _, _ ->
                    val builder = AlertDialog.Builder(this@ChooseSeatActivity)
                    builder.setTitle("请选择起始时间")

                    var startTimes : List<SeatTime>? = null
                    var endTimes : List<SeatTime>? = null

                    launch(UI) {
                        async(CommonPool) {
                            startTimes = client.getStartTimes(item.id,SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date()) )
                        }.await()

                        builder.setItems(startTimes!!.map { o -> o.name }.toTypedArray()) { _, which ->
                            launch(UI) {
                                async(CommonPool) {
                                    endTimes = client.getEndTimes(item.id, SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date()), startTimes!![which].id)
                                }.await()
                                val builder2 = AlertDialog.Builder(this@ChooseSeatActivity)
                                builder2.setTitle("选择结束时间")
                                builder2.setItems(endTimes!!.map { o -> o.name }.toTypedArray()) { _, which2 ->
                                    launch(UI) {
                                        var reservation : Reservation? = null
                                        async(CommonPool) {
                                            reservation = client.orderSeat(item.id,
                                                    SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date()),
                                                    startTimes!![which].id,
                                                    endTimes!![which2].id)
                                        }.await()
                                        val builder3 = AlertDialog.Builder(this@ChooseSeatActivity)
                                        builder3.setTitle(R.string.tip)
                                        builder3.setMessage("凭证号：${reservation!!.receipt}\n" +
                                                "地点：${reservation!!.location}\n" +
                                                "开始时间：${reservation!!.begin}\n" +
                                                "结束时间：${reservation!!.end}\n" +
                                                "状态：${reservation!!.status}")
                                        builder3.create().show()
                                    }
                                }

                                builder2.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss(); }
                                builder2.create().show()
                            }
                        }

                        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss(); }
                        builder.create().show()
                    }
                }

                initBuilder.create().show()

            }
        })

        val progressBar = findViewById(R.id.loading) as ProgressBar

        val buildingSpinner = findViewById(R.id.buildingSpinner) as Spinner
        val buildingAdapter = ArrayAdapter<Building>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                buildings
        )
        buildingSpinner.adapter = buildingAdapter

        val roomSpinner = findViewById(R.id.roomSpinner) as Spinner
        val roomAdapter = ArrayAdapter<Room>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                rooms
        )
        roomSpinner.adapter = roomAdapter

        val map = findViewById(R.id.map) as RecyclerView
        map.layoutManager = GridLayoutManager(this, 2)
        map.itemAnimator = DefaultItemAnimator()
        map.adapter = adapter


        progressBar.visibility = VISIBLE
        launch(UI) {
            async(CommonPool) {
                buildings.addAll(client.buildings)
            }.await()
            progressBar.visibility = GONE
            buildingAdapter.notifyDataSetChanged()
            if (buildings.size > 0) {
                buildingSpinner.setSelection(0)
            }
        }


        buildingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                progressBar.visibility = VISIBLE

                launch(UI) {
                    async(CommonPool) {
                        rooms.clear()
                        rooms.addAll(client.getRooms(buildings[position].id))
                    }.await()
                    progressBar.visibility = GONE
                    roomAdapter.notifyDataSetChanged()
                    if (rooms.size > 0) {
                        roomSpinner.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val scroll = findViewById(R.id.scrollView) as ScrollView2D

        roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                progressBar.visibility = VISIBLE

                launch(UI) {
                    async(CommonPool) {
                        layoutInfo = client.getSeatLayout(rooms[position].roomId,
                                SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date()))
                        scroll.scrollX = 0
                        scroll.scrollY = 0
                        seatLayout.clear()
                        seatLayout.addAll(layoutInfo!!.layout)
                    }.await()
                    map.layoutManager = GridLayoutManager(this@ChooseSeatActivity, layoutInfo!!.cols)
                    progressBar.visibility = GONE
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }
}