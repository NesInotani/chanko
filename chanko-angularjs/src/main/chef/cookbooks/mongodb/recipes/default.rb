#
# Cookbook Name:: mongodb
# Recipe:: default
#
# Copyright 2014, Nobunaga Yasuda
#
# All rights reserved - Do Not Redistribute
#

# Package install

cookbook_file "/etc/yum.repos.d/mongodb.repo" do
  mode 00644
end

package "mongodb-org" do
  action :install
end

service "mongod" do
  action [:enable, :start]
end
