// Define API function declspec/attributes and how each supported compiler or OS
// allows us to specify them.
#if defined __GNUC__
  #define _kmn_and ,
  #define _kmn_tag_fn(a)        __attribute__((a))
  #define _kmn_deprecated_flag  deprecated
  #define _kmn_export_flag      visibility("default")
  #define _kmn_import_flag      visibility("default")
  #define _kmn_static_flag      visibility("hidden")
#endif

#if defined _WIN32 || defined __CYGWIN__
  #if defined __GNUC__  // These three will be redefined for Windows
    #undef _kmn_export_flag
    #undef _kmn_import_flag
    #undef _kmn_static_flag
  #else  // How MSVC sepcifies function level attributes adn deprecation
    #define _kmn_and
    #define _kmn_tag_fn(a)       __declspec(a)
    #define _kmn_deprecated_flag deprecated
  #endif
  #define _kmn_export_flag     dllexport
  #define _kmn_import_flag     dllimport
  #define _kmn_static_flag
#endif

#if defined GRAPHITE2_STATIC
  #define KMN_API             _kmn_tag_fn(_kmn_static_flag)
  #define KMN_DEPRECATED_API  _kmn_tag_fn(_kmn_deprecated_flag _kmn_and _kmn_static_flag)
#elif defined GRAPHITE2_EXPORTING
  #define KMN_API             _kmn_tag_fn(_kmn_export_flag)
  #define KMN_DEPRECATED_API  _kmn_tag_fn(_kmn_deprecated_flag _kmn_and _kmn_export_flag)
#else
  #define KMN_API             _kmn_tag_fn(_kmn_import_flag)
  #define KMN_DEPRECATED_API  _kmn_tag_fn(_kmn_deprecated_flag _kmn_and _kmn_import_flag)
#endif
