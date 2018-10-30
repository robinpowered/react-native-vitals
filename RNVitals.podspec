require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name            = package['name']
  s.version         = package['version']
  s.summary         = package['description']
  s.license         = package['license']
  s.homepage        = package['homepage']
  s.author          = { package['author']['name'] => package['author']['email'] }
  s.source          = { :git => package['repository']['url'], :tag => "v#{s.version}" }
  s.source_files    = 'ios/*.{h,m}'
  s.platform        = :ios, '8.0'

  s.dependency        'React'
end
