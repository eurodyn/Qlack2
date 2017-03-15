'use strict';

var path = require('path');
var gulp = require('gulp');
var conf = require('./conf');
var gutil = require('gulp-util');
const size = require('gulp-size');
var debug = require('gulp-debug');

var $ = require('gulp-load-plugins')({
  pattern: ['gulp-*', 'main-bower-files', 'uglify-save-license', 'del']
});

gulp.task('partials', function () {
  return gulp.src([
    path.join(conf.paths.src, '/app/**/*.html'),
    path.join(conf.paths.tmp, '/serve/app/**/*.html')
  ])
    //.pipe($.replace('bower_components/kavmodern-web-swiss-styleguide-internal/build/' ,'/'))
    .pipe($.htmlmin({
      removeEmptyAttributes: true,
      removeAttributeQuotes: true,
      collapseBooleanAttributes: true,
      collapseWhitespace: true
    }))
    .pipe($.angularTemplatecache('templateCacheHtml.js', {
      module: 'kavModernWebIps',
      root: 'app'
    }))
    .pipe(gulp.dest(conf.paths.tmp + '/partials/'));
});

gulp.task('html', ['inject', 'partials'], function () {
  var partialsInjectFile = gulp.src(path.join(conf.paths.tmp, '/partials/templateCacheHtml.js'), { read: false });
  var partialsInjectOptions = {
    starttag: '<!-- inject:partials -->',
    ignorePath: path.join(conf.paths.tmp, '/partials'),
    addRootSlash: false
  };

  var htmlFilter = $.filter('*.html', { restore: true });
  var jsFilter = $.filter('**/*.js', { restore: true });
  var cssFilter = $.filter('**/*.css', { restore: true });

  return gulp.src(path.join(conf.paths.tmp, '/serve/*.html'))
    .pipe(debug({title: 'inject -----------------------'}))
    .pipe($.inject(partialsInjectFile, partialsInjectOptions))

    .pipe(debug({title: 'useref -----------------------'}))
    .pipe($.useref())

    .pipe(debug({title: 'jsFilter -----------------------'}))
    .pipe(jsFilter)

    .pipe(debug({title: 'sourcemaps -----------------------'}))
    .pipe($.sourcemaps.init())

    .pipe(debug({title: 'ngAnnotate -----------------------'}))
    .pipe($.ngAnnotate())

    // .pipe(debug({title: 'uglify -----------------------'}))
    // .pipe($.uglify({ preserveComments: $.uglifySaveLicense })).on('error', conf.errorHandler('Uglify'))

    .pipe(debug({title: 'rev1 -----------------------'}))
    .pipe($.rev())

    .pipe(debug({title: 'sourcemaps -----------------------'}))
    .pipe($.sourcemaps.write('maps'))

    .pipe(debug({title: 'jsFilter -----------------------'}))
    .pipe(jsFilter.restore)

    .pipe(debug({title: 'cssFilter1 -----------------------'}))
    .pipe(cssFilter)

    .pipe(debug({title: 'replace -----------------------'}))
    // .pipe($.sourcemaps.init())
    .pipe($.replace('../../bower_components/bootstrap/fonts/', '../fonts/'))

    // .pipe(debug({title: 'cssnano -----------------------'}))
    // .pipe($.cssnano())

    .pipe(debug({title: 'rev2 -----------------------'}))
    .pipe($.rev())

    .pipe(debug({title: 'cssFilter2 -----------------------'}))
    // .pipe($.sourcemaps.write('maps'))
    .pipe(cssFilter.restore)

    .pipe(debug({title: 'revReplace -----------------------'}))

    .pipe(debug({title: 'htmlFilter -----------------------'}))
    .pipe(htmlFilter)
    //.pipe($.replace('bower_components/kavmodern-web-swiss-styleguide-internal/build/' ,'/'))

    // .pipe(debug({title: 'htmlmin -----------------------'}))
    // .pipe($.htmlmin({
    //   removeEmptyAttributes: true,
    //   removeAttributeQuotes: true,
    //   collapseBooleanAttributes: true,
    //   collapseWhitespace: true
    // }))

    .pipe(debug({title: 'htmlFilter.restore -----------------------'}))
    .pipe(htmlFilter.restore)

    .pipe(debug({title: 'dest -----------------------'}))
    .pipe(gulp.dest(path.join(conf.paths.dist, '/')))

    .pipe(debug({title: 'size -----------------------'}))
    .pipe($.size({ title: path.join(conf.paths.dist, '/'), showFiles: true }));
  });

// Only applies for fonts from bower dependencies
// Custom fonts are handled by the "other" task
gulp.task('fonts', function () {
  return gulp.src($.mainBowerFiles())
    .pipe($.filter('**/*.{eot,otf,svg,ttf,woff,woff2}'))
    .pipe($.flatten())
    .pipe(gulp.dest(path.join(conf.paths.dist, '/fonts/')));
});

gulp.task('other', function () {
  var fileFilter = $.filter(function (file) {
    return file.stat.isFile();
  });

  return gulp.src([
    path.join(conf.paths.src, '/**/*'),
    // path.join('!' + conf.paths.src, '/**/*.{html,css,js,less}')
    path.join('!' + conf.paths.src, '/**/*.{html,js,less}')
  ])
    .pipe(fileFilter)
    .pipe(gulp.dest(path.join(conf.paths.dist, '/')));
});

gulp.task('clean', function () {
  return $.del([path.join(conf.paths.dist, '/'), path.join(conf.paths.tmp, '/'), path.join(conf.paths.webPackage, '/')]);
});

gulp.task('ckopy', function() {
  return gulp.src(['ckplugins/**/*'], {
    base: 'ckplugins'
  }).pipe(gulp.dest('bower_components/ckeditor/plugins'));
});

gulp.task('web-package', ['clean', 'build'], function() {
  gulp.src('bower_components/**/*').pipe(gulp.dest(conf.paths.webPackageBower));
  gulp.src('.tmp/serve/**/*').pipe(gulp.dest(conf.paths.webPackage))
  gulp.src(['src/**/*', '!src/index.html']).pipe(gulp.dest(conf.paths.webPackage));
});

gulp.task('build', ['html', 'fonts', 'other', 'ckopy']);
