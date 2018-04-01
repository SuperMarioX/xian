package com.apifest.oauth20.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.support.authen.AccessToken;

/**
 * @author happyyangyuan
 */
public class ValidateAccessTokenUnit implements Unit {
    @Override
    public String getName() {
        return "validateAccessToken";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("验证accessToken正确性")
                .setPublic(true)
                .setSecure(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("accessToken", String.class, "token字符串", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        AccessToken token = OAuthService.auth.isValidToken(msg.get("accessToken", String.class));
        if (token != null) {
            return UnitResponse.createSuccess(token);
        } else {
            return UnitResponse.createUnknownError(null, "token required.");
        }
    }

    @Override
    public Group getGroup() {
        return OAuthService.singleton;
    }
}
