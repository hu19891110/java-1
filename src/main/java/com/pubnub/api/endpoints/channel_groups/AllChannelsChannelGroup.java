package com.pubnub.api.endpoints.channel_groups;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.builder.PubNubErrorBuilder;
import com.pubnub.api.endpoints.Endpoint;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.channel_group.PNChannelGroupsAllChannelsResult;
import com.pubnub.api.models.server.Envelope;
import lombok.Setter;
import lombok.experimental.Accessors;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Accessors(chain = true, fluent = true)
public class AllChannelsChannelGroup extends Endpoint<Envelope<Object>, PNChannelGroupsAllChannelsResult> {
    @Setter
    private String channelGroup;

    public AllChannelsChannelGroup(PubNub pubnub, Retrofit retrofit) {
        super(pubnub, retrofit);
    }

    @Override
    protected void validateParams() throws PubNubException {
        if (channelGroup == null || channelGroup.isEmpty()) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_GROUP_MISSING).build();
        }
    }

    @Override
    protected Call<Envelope<Object>> doWork(Map<String, String> params) {
        ChannelGroupService service = this.getRetrofit().create(ChannelGroupService.class);

        return service.allChannelsChannelGroup(this.getPubnub().getConfiguration().getSubscribeKey(), channelGroup, params);
    }

    @Override
    protected PNChannelGroupsAllChannelsResult createResponse(Response<Envelope<Object>> input) throws PubNubException {
        Map<String, Object> stateMappings;

        if (input.body() == null || input.body().getPayload() == null) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_PARSING_ERROR).build();
        }

        stateMappings = (Map<String, Object>) input.body().getPayload();
        List<String> channels = (ArrayList<String>) stateMappings.get("channels");

        return PNChannelGroupsAllChannelsResult.builder()
                .channels(channels)
                .build();
    }

    protected int getConnectTimeout() {
        return this.getPubnub().getConfiguration().getConnectTimeout();
    }

    protected int getRequestTimeout() {
        return this.getPubnub().getConfiguration().getNonSubscribeRequestTimeout();
    }

    @Override
    protected PNOperationType getOperationType() {
        return PNOperationType.PNChannelsForGroupOperation;
    }

    @Override
    protected boolean isAuthRequired() {
        return true;
    }

}
