package ru.vladislavsumin.cams.ui.cams.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_cam_detail.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.entity.CameraDAO
import ru.vladislavsumin.cams.ui.ToolbarActivity
import com.arellomobile.mvp.presenter.InjectPresenter


class CamDetailActivity : ToolbarActivity(), CamDetailsView {
    companion object {
        private const val LAYOUT = R.layout.activity_cam_detail

        private const val MENU_SAVE_ITEM = 1
        private const val MENU_DELETE_ITEM = 2

        const val RESULT_SAVE_OR_UPDATE = 1
        const val RESULT_DELETE = 2

        const val EXTRA_CAM_DETAILS = "extra_cam_details"

        fun getLaunchIntent(context: Context, camera: CameraDAO? = null): Intent {
            val intent = Intent(context, CamDetailActivity::class.java)
            intent.putExtra(EXTRA_CAM_DETAILS, camera)
            return intent
        }
    }

    private lateinit var mSaveBtn: MenuItem
    private var mDeleteBtn: MenuItem? = null

    private var camera: CameraDAO? = null

    @InjectPresenter
    lateinit var mPresenter: CamDetailsPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(LAYOUT)
        super.onCreate(savedInstanceState)

        val camera = intent.getParcelableExtra<CameraDAO>(EXTRA_CAM_DETAILS)
        if (camera != null) setCamera(camera)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        if (camera != null) {
            mDeleteBtn = menu.add(Menu.NONE, MENU_DELETE_ITEM, Menu.NONE, getString(R.string.delete))
            mDeleteBtn!!
                .setIcon(R.drawable.ic_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        mSaveBtn = menu.add(Menu.NONE, MENU_SAVE_ITEM, Menu.NONE, R.string.save)
        mSaveBtn
            .setIcon(R.drawable.ic_save)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_SAVE_ITEM -> {
                if (verifyCamera()) mPresenter.onClickSave(getCamera())
                return true
            }
            MENU_DELETE_ITEM -> {
                mPresenter.onClickDelete(camera!!)
                return true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun setButtonsEnabled(enabled: Boolean) {
        if (enabled) {
            mSaveBtn.isEnabled = true
            tintMenuItemIcon(R.color.black, mSaveBtn)

            mDeleteBtn?.isEnabled = true
            tintMenuItemIcon(R.color.black, mDeleteBtn)
        } else {
            mSaveBtn.isEnabled = false
            tintMenuItemIcon(R.color.grey, mSaveBtn)

            mDeleteBtn?.isEnabled = false
            tintMenuItemIcon(R.color.grey, mDeleteBtn)
        }
    }

    override fun finishWithSaveResult(camera: CameraDAO) {
        setResult(RESULT_SAVE_OR_UPDATE, Intent().putExtra(EXTRA_CAM_DETAILS, camera))
        finish()
    }

    override fun finishWithDeleteResult(camera: CameraDAO) {
        setResult(RESULT_DELETE, Intent().putExtra(EXTRA_CAM_DETAILS, camera))
        finish()
    }

    private fun setCamera(camera: CameraDAO) {
        this.camera = camera
        cam_name.editText!!.setText(camera.name)
        cam_ip_address.editText!!.setText(camera.ip)
        cam_port.editText!!.setText(camera.port.toString())
        cam_login.editText!!.setText(camera.login)
    }

    private fun verifyCamera(): Boolean {
        var result = true

        //TODO move to string resources
        //TODO need refactor
        if (cam_name.editText!!.text.toString().isEmpty()) {
            result = false
            cam_name.error = "Name must be not empty"
        } else {
            cam_name.error = null
        }

        if (cam_ip_address.editText!!.text.toString().isEmpty()) {
            result = false
            cam_ip_address.error = "IP address must be not empty"
        } else {
            cam_ip_address.error = null
        }

        if (cam_port.editText!!.text.toString().isEmpty()) {
            result = false
            cam_port.error = "port must be not empty"
        } else {
            cam_port.error = null
        }

        if (cam_login.editText!!.text.toString().isEmpty()) {
            result = false
            cam_login.error = "login must be not empty"
        } else {
            cam_login.error = null
        }

        return result
    }

    private fun getCamera(): CameraDAO {
        return (this.camera ?: CameraDAO())
            .copy(
                name = cam_name.editText!!.text.toString(),
                ip = cam_ip_address.editText!!.text.toString(),
                port = cam_port.editText!!.text.toString().toInt(),
                login = cam_login.editText!!.text.toString(),
                password = cam_password.editText!!.text.toString()
            )
    }
}
