const path = require('path');
const ESLintPlugin = require('eslint-webpack-plugin');
const CopyPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (env, options) => {
  const outputPath = path.resolve(__dirname, 'build');
  return {
    entry: './src/index.js',
    output: {
      path: outputPath,
      filename: 'index.js'
    },
    module: {
      rules: [
        {
          enforce: 'pre',
          test: /\.js$/,
          exclude: /node_modules/,
          loader: 'source-map-loader',
        },
        {
          test: /\.js$/,
          use: [
            {
              loader: 'babel-loader',
              options: {
                presets: [
                  '@babel/preset-env',
                  ['@babel/preset-react', {runtime: 'automatic'}]
                ]
              }
            }
          ],
          exclude: /node_modules/,
        },
        {
          test: /\.css$/,
          use: [
            MiniCssExtractPlugin.loader,
            'css-loader',
            'postcss-loader'
          ],
        },
      ]
    },
    plugins: [
      new ESLintPlugin({exclude: 'node_modules'}),
      new CopyPlugin({
        patterns: [
          {from: './public'}
        ]
      }),
      new MiniCssExtractPlugin(),
      new HtmlWebpackPlugin({
        title: 'SQL Quiz',
        template: './src/index.html',
        favicon: './src/assets/favicon.ico'
      })
    ],
    devtool: options.mode === 'development' ? 'source-map' : 'source-map',
    devServer: {
      port: 9000,
      open: '/',
      historyApiFallback: true
    },
    target: 'web'
  };
};