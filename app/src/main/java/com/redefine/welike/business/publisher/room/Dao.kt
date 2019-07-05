package com.redefine.welike.business.publisher.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface DraftDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(draft: Draft)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(draft: Draft)

    @Delete
    fun delete(draft: Draft)

    @Query("DELETE FROM t_draft WHERE did=:draftId")
    fun delete(draftId: String)

    @Query("DELETE FROM t_draft WHERE UID=:uid")
    fun deleteAll(uid: String)

    @Update
    fun update(draft: Draft)

    @Query("SELECT * FROM t_draft WHERE did = :did AND uid =:uid")
    fun getDraft(did: String, uid: String): Draft

    @Query("SELECT * FROM t_draft WHERE uid=:uid AND visibility=:visibility ORDER BY time DESC LIMIT :limit")
    fun getAll(uid: String, visibility: Boolean, limit: Int): LiveData<List<Draft>>

    @Query("SELECT * FROM t_draft WHERE uid=:uid  ORDER BY time DESC")
    fun getAll(uid: String): List<Draft>

    @Query("SELECT COUNT(did) as total FROM t_draft WHERE uid=:uid AND visibility=:visibility")
    fun getDraftCount(uid: String, visibility: Boolean): DraftCount

    @Query("SELECT COUNT(did) as total FROM t_draft WHERE uid=:uid AND visibility=:visibility")
    fun getDraftCount1(uid: String, visibility: Boolean): LiveData<DraftCount>

    @Query("SELECT * FROM t_draft WHERE uid=:uid  AND type=:type")
    fun getUploadingDraft(uid: String, type: Int): LiveData<List<Draft>>

}

