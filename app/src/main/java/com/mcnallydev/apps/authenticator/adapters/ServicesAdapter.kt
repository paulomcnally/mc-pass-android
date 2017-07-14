package com.mcnallydev.apps.authenticator.adapters

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.mcnallydev.apps.authenticator.R
import com.mcnallydev.apps.authenticator.models.ServiceModel

import io.realm.RealmResults
import android.content.Context.CLIPBOARD_SERVICE
import com.mcnallydev.apps.authenticator.Config
import com.tumblr.remember.Remember
import android.provider.SyncStateContract.Helpers.update
import java.security.MessageDigest
import android.R.attr.data
import org.apache.commons.codec.digest.DigestUtils
import java.security.NoSuchAlgorithmException
import android.R.attr.data
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import io.realm.Realm
import org.apache.commons.codec.binary.Hex
import io.realm.RealmObject.deleteFromRealm






class ServicesAdapter( private val context: Context, private val data: RealmResults<ServiceModel>) : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: RelativeLayout = view.findViewById<RelativeLayout>(R.id.service_item)
        var service: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.service)
        var username: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.username)
        var character: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.character)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ServicesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_service, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serviceModel = data[position]
        holder.service.text = serviceModel.name
        holder.username.text = serviceModel.username
        holder.character.text = serviceModel.name?.substring(0,1)

        holder.item.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", hash(serviceModel.name as String, serviceModel.username as String))
            clipboard.primaryClip = clip
            Toast.makeText(context, R.string.clipboard_message, Toast.LENGTH_LONG).show()
        }

        holder.item.setOnLongClickListener {
            MaterialDialog.Builder(context)
                    .title(R.string.dialog_delete_title)
                    .content(R.string.dialog_delete_content)
                    .positiveText(R.string.dialog_ok)
                    .negativeText(R.string.dialog_cancel)
                    .onPositive { _, _ ->
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val service = data[position]
                            service.deleteFromRealm()
                        }
                        notifyDataSetChanged()
                    }
                    .show()
            true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun hash(service: String, username: String): String? {
        val salt = Remember.getString(Config.SALT, "")

        // https://goo.gl/PLbpSb
        val string = String.format("%s_%s_%s", salt, service, username)

        val hash = String(Hex.encodeHex(DigestUtils.md5(string)))
        return hash
    }
}