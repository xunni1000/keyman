KeymanWeb.KR(new Keyboard_platformtest());function Keyboard_platformtest(){this.KI="Keyboard_platformtest";this.KN="PlatformTest";this.KMINVER="9.0";this.KV=null;this.KH='';this.KM=0;this.KBVER="1.0";this.KMBM=0x0010;this.s7="touch";this.s8="hardware";this.s9="windows";this.s10="android";this.s11="iOS";this.s12="macosx";this.s13="linux";this.s14="desktop";this.s15="tablet";this.s16="phone";this.s17="native";this.s18="web";this.s19="ie";this.s20="chrome";this.s21="firefox";this.s22="safari";this.s23="opera";this.s24="touch";this.s25="hardware";this.KVER="10.0.1103.0";this.gs=function(t,e) {return this.g0(t,e);};this.g0=function(t,e) {var k=KeymanWeb,r=0,m=0;if(k.KKM(e,16400,80)&&!k.KIFS(31,this.s24,t)) {r=m=1;k.KO(0,t," !Touch");}else if(k.KKM(e,16400,80)&&!k.KIFS(31,this.s25,t)) {r=m=1;k.KO(0,t," !Hardware");}else if(k.KKM(e,16384,73)&&k.KIFS(31,this.s14,t)) {r=m=1;k.KO(0,t," Desktop");}else if(k.KKM(e,16384,73)&&k.KIFS(31,this.s15,t)) {r=m=1;k.KO(0,t," Tablet");}else if(k.KKM(e,16384,73)&&k.KIFS(31,this.s16,t)) {r=m=1;k.KO(0,t," Phone");}else if(k.KKM(e,16384,73)) {r=m=1;k.KO(0,t," [FF Undefined]");}else if(k.KKM(e,16384,79)&&k.KIFS(31,this.s9,t)) {r=m=1;k.KO(0,t," Windows");}else if(k.KKM(e,16384,79)&&k.KIFS(31,this.s10,t)) {r=m=1;k.KO(0,t," Android");}else if(k.KKM(e,16384,79)&&k.KIFS(31,this.s11,t)) {r=m=1;k.KO(0,t," iOS");}else if(k.KKM(e,16384,79)&&k.KIFS(31,this.s12,t)) {r=m=1;k.KO(0,t," OSX");}else if(k.KKM(e,16384,79)&&k.KIFS(31,this.s13,t)) {r=m=1;k.KO(0,t," Linux");}else if(k.KKM(e,16384,79)) {r=m=1;k.KO(0,t," [OS Undefined]");}else if(k.KKM(e,16384,80)&&k.KIFS(31,this.s7,t)) {r=m=1;k.KO(0,t,"Touch");}else if(k.KKM(e,16384,80)&&k.KIFS(31,this.s8,t)) {r=m=1;k.KO(0,t,"Hardware");}else if(k.KKM(e,16384,80)) {r=m=1;k.KO(0,t," [UI Undefined]");}else if(k.KKM(e,16384,85)&&k.KIFS(31,this.s17,t)) {r=m=1;k.KO(0,t," Native");}else if(k.KKM(e,16384,85)&&k.KIFS(31,this.s18,t)) {r=m=1;k.KO(0,t," Web");}else if(k.KKM(e,16384,85)) {r=m=1;k.KO(0,t," [Nativeness Undefined]");}else if(k.KKM(e,16384,89)&&k.KIFS(31,this.s19,t)) {r=m=1;k.KO(0,t," IE");}else if(k.KKM(e,16384,89)&&k.KIFS(31,this.s20,t)) {r=m=1;k.KO(0,t," Chrome");}else if(k.KKM(e,16384,89)&&k.KIFS(31,this.s21,t)) {r=m=1;k.KO(0,t," Firefox");}else if(k.KKM(e,16384,89)&&k.KIFS(31,this.s22,t)) {r=m=1;k.KO(0,t," Safari");}else if(k.KKM(e,16384,89)&&k.KIFS(31,this.s23,t)) {r=m=1;k.KO(0,t," Opera");}else if(k.KKM(e,16384,89)) {r=m=1;k.KO(0,t," [Browser Undefined]");}return r;};}