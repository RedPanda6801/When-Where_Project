package com.example.whenandwhere

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // 테스트 API
    @GET("api/test")
    fun testData(): Call<ObjectDto>

    // auth, 유저 API
    @POST("api/oauth/sign-check")
    fun oauthCheck(@Body string : String) : Call<LoginDto>

    @GET("api/user/get-user")
    fun getUser() : Call<ObjectDto>

    @POST("api/user/sign-up")
    fun signUp(@Body userDto : UserDto) : Call<ObjectDto>

    // 그룹 및 그룹 멤버 API
    @GET("api/group/get-my-groups")
    fun getMyGroups() : Call<GroupListDto>

    @POST("api/group/create")
    fun createGroup(@Body groupDto : GroupDto) : Call<ObjectDto>

    @GET("api/group/get-members/{group_id}")
    fun getGroupMembers(@Path("group_id", encoded = true) groupId: Int): Call<MemberListDto>

    @POST("api/group/delete")
    fun deleteGroup(@Body groupDto: GroupDto) : Call<ObjectDto>

    @POST("api/group/emit")
    fun emitMember(@Body applyDto: ApplyDto) : Call<ObjectDto>

    @POST("api/group/exit/{group_id}")
    fun exitGroup(@Path("group_id", encoded = true) groupId: Int): Call<ObjectDto>

    // 스케줄 API
    @GET("api/schedule/get-schedule")
    fun getSchedules(@Query("userId") userId : String) : Call<ScheduleListDto>

    @POST("api/schedule/add/{email}")
    fun addSchedule(@Body scheduleDto: ScheduleDto, @Path("email") email : String) : Call<ObjectDto>

    // 그룹 지원 API
    @GET("api/apply/get-apply/{group_id}")
    fun getApplies(@Path("group_id", encoded = true) groupId: Int): Call<ApplyListDto>

    @POST("api/apply/process")
    fun processApply(@Body applyDto: ApplyDto) : Call<ObjectDto>

    @POST("api/schedule/delete")
    fun deleteSchedule(@Body scheduleDto: ScheduleDto) : Call<ObjectDto>

    @GET("text/{time}/{address}/{drink}")
    fun aiRecommend(@Path("time") time : Int, @Path("address") address : String, @Path("drink") drink : Boolean) : Call<AIResultDto>

    @POST("api/result/add-result")
    fun addRecommend(@Body recommend : RecommendResult) :Call<ObjectDto>

    @GET("api/result/get-result/{groupId}")
    fun getRecommend(@Path("groupId") groupId : Int) : Call<RecommendResultDto>

    @POST("api/schedule/busytime-group")
    fun calcSchedule(@Body busyTimeDto : BusyTimeDto) : Call<ScheduleCalcListDto>
}