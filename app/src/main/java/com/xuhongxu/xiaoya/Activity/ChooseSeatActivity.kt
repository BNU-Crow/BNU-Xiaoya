package com.xuhongxu.xiaoya.Activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.xuhongxu.LibrarySeat.*
import com.xuhongxu.xiaoya.Adapter.SeatLayoutItemRecycleAdapter
import com.xuhongxu.xiaoya.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*
import com.xuhongxu.xiaoya.View.ScrollView2D



class ChooseSeatActivity : AppCompatActivity() {

    var todaySwitch: Switch? = null
    var queryDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())!!

    var layoutInfo: SeatLayout? = null
    val seatLayout = ArrayList<SeatLayoutItem>()

    val buildings = ArrayList<Building>()
    val rooms = ArrayList<Room>()

    var progressBar: ProgressBar? = null
    var client: SeatClient? = null
    var scroll: ScrollView2D? = null
    var map: RecyclerView? = null
    var adapter: SeatLayoutItemRecycleAdapter? = null

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

        client = SeatClient(username, password)
        client!!.token = intent.getStringExtra("Client")

        adapter = SeatLayoutItemRecycleAdapter(this, seatLayout, object : SeatLayoutItemRecycleAdapter.OnClickListener {
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
                            startTimes = client!!.getStartTimes(item.id,queryDate )
                        }.await()

                        builder.setItems(startTimes!!.map { o -> o.name }.toTypedArray()) { _, which ->
                            launch(UI) {
                                async(CommonPool) {
                                    endTimes = client!!.getEndTimes(item.id, queryDate, startTimes!![which].id)
                                }.await()
                                val builder2 = AlertDialog.Builder(this@ChooseSeatActivity)
                                builder2.setTitle("选择结束时间")
                                builder2.setItems(endTimes!!.map { o -> o.name }.toTypedArray()) { _, which2 ->
                                    launch(UI) {
                                        var reservation : Reservation? = null
                                        async(CommonPool) {
                                            reservation = client!!.orderSeat(item.id,
                                                    queryDate,
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

        progressBar = findViewById(R.id.loading)

        val buildingSpinner = findViewById<Spinner>(R.id.buildingSpinner)
        val buildingAdapter = ArrayAdapter<Building>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                buildings
        )
        buildingSpinner.adapter = buildingAdapter

        val roomSpinner = findViewById<Spinner>(R.id.roomSpinner)
        val roomAdapter = ArrayAdapter<Room>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                rooms
        )
        roomSpinner.adapter = roomAdapter

        map = findViewById(R.id.map)
        map!!.layoutManager = GridLayoutManager(this, 2)
        map!!.itemAnimator = DefaultItemAnimator()
        map!!.adapter = adapter


        progressBar!!.visibility = VISIBLE
        launch(UI) {
            async(CommonPool) {
                buildings.addAll(client!!.buildings)
            }.await()
            progressBar!!.visibility = GONE
            buildingAdapter.notifyDataSetChanged()
            if (buildings.size > 0) {
                buildingSpinner.setSelection(0)
            }
        }

        todaySwitch = findViewById(R.id.today_switch)
        todaySwitch!!.setOnCheckedChangeListener { _, b ->
            if (b) {
                var tomorrow = Date()
                val c = Calendar.getInstance()
                c.time = tomorrow
                c.add(Calendar.DATE, 1)
                tomorrow = c.time
                queryDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(tomorrow)!!
            } else {
                queryDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())!!
            }
            updateScroll(roomSpinner.selectedItemPosition)
        }


        buildingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                progressBar!!.visibility = VISIBLE

                launch(UI) {
                    async(CommonPool) {
                        rooms.clear()
                        rooms.addAll(client!!.getRooms(buildings[position].id))
                    }.await()
                    progressBar!!.visibility = GONE
                    roomAdapter.notifyDataSetChanged()
                    if (rooms.size > 0) {
                        roomSpinner.setSelection(0)
                        updateScroll(0)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        scroll = findViewById(R.id.scrollView)

        roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                updateScroll(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }

    fun updateScroll(position: Int) {
        progressBar!!.visibility = VISIBLE

        launch(UI) {
            async(CommonPool) {
                layoutInfo = client!!.getSeatLayout(rooms[position].roomId,
                        queryDate)
                scroll!!.scrollX = 0
                scroll!!.scrollY = 0
                seatLayout.clear()
                seatLayout.addAll(layoutInfo!!.layout)
            }.await()
            map!!.layoutManager = GridLayoutManager(this@ChooseSeatActivity, layoutInfo!!.cols)
            progressBar!!.visibility = GONE
            adapter!!.notifyDataSetChanged()
        }
    }
}
