#
# Cookbook Name:: vsftpd
# Recipe:: default
#
# Copyright 2014, Nobunaga Yasuda
#
# All rights reserved - Do Not Redistribute
#

service "iptables" do
  action [:disable, :stop]
end

# Package install
package "vim" do
  action :install
end

package "ftp" do
  action :install
end

package "vsftpd" do
  action :install
end

service "vsftpd" do
  action [:enable, :start]
end

group "ftpgrp1" do
  action :create
end

user "ftpusr1" do
  action :create
  password "$1$xwYOo5cH$BWZG8l6GFQuyhXx6moqxf1"
  gid "ftpgrp1"
end

cookbook_file "/etc/vsftpd/vsftpd.conf" do
  mode 0644
end