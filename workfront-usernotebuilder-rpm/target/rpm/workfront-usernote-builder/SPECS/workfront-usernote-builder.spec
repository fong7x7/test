%define _unpackaged_files_terminate_build 0
%define _disable_jar_repacking 1
%define __jar_repack 0
Name: workfront-usernote-builder
Version: 0.0.1
Release: SNAPSHOT20150302173343
Summary: User Notification Builder Service RPM
License: (c) 2015, Workfront
Vendor: Workfront
URL: http://www.workfront.com/
Group: Applications/Services
Packager: Workfront
autoprov: yes
autoreq: yes
Prefix: /opt/attask/usernotebuilder
BuildArch: noarch
BuildRoot: /Users/normanfong/dev/attask/attask-usernote-builder/workfront-usernotebuilder-rpm/target/rpm/workfront-usernote-builder/buildroot

%description
Workfront User Notification Builder Service

%install

if [ -d $RPM_BUILD_ROOT ];
then
  mv /Users/normanfong/dev/attask/attask-usernote-builder/workfront-usernotebuilder-rpm/target/rpm/workfront-usernote-builder/tmp-buildroot/* $RPM_BUILD_ROOT
else
  mv /Users/normanfong/dev/attask/attask-usernote-builder/workfront-usernotebuilder-rpm/target/rpm/workfront-usernote-builder/tmp-buildroot $RPM_BUILD_ROOT
fi
chmod -R +w $RPM_BUILD_ROOT

ln -s ../../../opt/attask/usernotebuilder/bin/usernotebuilder.sh $RPM_BUILD_ROOT/etc/rc.d/init.d/usernotebuilder

%files
%defattr(644,jms,jms,755)
%dir  "/opt/attask/usernotebuilder"
 "/opt/attask/usernotebuilder/lib"
%dir  "/opt/attask/usernotebuilder/logs"
%attr(755,jms,jms) "/opt/attask/usernotebuilder/bin"
%config(noreplace)  "/opt/attask/usernotebuilder/conf"
%attr(644,root,root)  "/etc/rc.d/init.d/usernotebuilder"

%post
chkconfig --add usernotebuilder

%preun
/etc/init.d/usernotebuilder stop
						chkconfig --del usernotebuilder
